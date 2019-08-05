package demoschema.orm;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import com.google.common.collect.ImmutableMap;
import demoschema.orm.NotPortable.Cause;
import java.sql.Connection;
import java.util.Comparator;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.hibernate.Session;
import org.hibernate.cfg.Environment;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.testcontainers.containers.PostgreSQLContainer;

/** Integration tests using Hibernate and a real database. */
@RunWith(JUnit4.class)
public class HibernateIntegrationTest {

  private static Optional<PostgreSQLContainer<?>> inMemoryTestDatabase = Optional.empty();
  private static EntityManagerFactory entityManagerFactory;

  @BeforeClass
  public static void setupEntityManagerFactory() {
    if (System.getProperty("jdbcUrl") != null) {
      entityManagerFactory =
          createEntityManagerFactory(
              checkNotNull(System.getProperty("jdbcUrl"), "jdbcUrl"),
              checkNotNull(System.getProperty("dbUser"), "dbUser"),
              checkNotNull(System.getProperty("dbPassword", "dbPassword")));
      System.out.printf("Will connect to external database at %s\n", System.getProperty("jdbcUrl"));
      return;
    }
    inMemoryTestDatabase =
        Optional.of(
            new PostgreSQLContainer<>("postgres:9.6.12")
                .withInitScript("demoschema/hibernate/demo_schema.golden"));
    inMemoryTestDatabase.get().start();
    entityManagerFactory =
        createEntityManagerFactory(
            inMemoryTestDatabase.get().getJdbcUrl(),
            inMemoryTestDatabase.get().getUsername(),
            inMemoryTestDatabase.get().getPassword());
    System.out.printf("Will connect to in-memory test database.\n");
  }

  private static EntityManagerFactory createEntityManagerFactory(
      String jdbcUrl, String dbUser, String dbPassword) {
    return Persistence.createEntityManagerFactory(
        "nomulus",
        ImmutableMap.of(
            Environment.JPA_JDBC_URL, jdbcUrl,
            Environment.JPA_JDBC_USER, dbUser,
            Environment.JPA_JDBC_PASSWORD, dbPassword,
            Environment.SHOW_SQL, "true"));
  }

  @AfterClass
  public static void tearDown() {
    entityManagerFactory.close();
    inMemoryTestDatabase.ifPresent(PostgreSQLContainer::stop);
  }

  @Test
  @NotPortable(cause = Cause.ORM, details = "Session")
  public void testIsolationLevel() {
    // TODO(weiminyu): implement an autocloseable entity manager.
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    Session session = entityManager.unwrap(Session.class);
    int isolationLevel =
        session.doReturningWork(connection -> connection.getTransactionIsolation());
    entityManager.getTransaction().commit();
    // Isolation level is defined in persistence.xml
    assertThat(isolationLevel).isEqualTo(Connection.TRANSACTION_SERIALIZABLE);
  }

  @Test
  public void testDomain() {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    Optional<String> lastEppRepoId =
        entityManager.createQuery("from Domain", Domain.class).getResultList().stream()
            .sorted(Comparator.comparing(Domain::getEppRepoId).reversed())
            .findFirst()
            .map(Domain::getEppRepoId);
    Domain entity = new Domain();
    entity.setEppRepoId(lastEppRepoId.orElse("") + "1");
    entityManager.persist(entity);
    entityManager.getTransaction().commit();
    entityManager.close();
  }

  @Test
  public void testHost() {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    Host entity = new Host();
    entityManager.persist(entity);
    entityManager.flush();
    entityManager.getTransaction().commit();
    entityManager.close();
  }

  @Test
  public void testRegistryLock() {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    RegistryLock entity = createRegistryLock(LockAction.LOCK);
    entityManager.persist(entity);
    entityManager.persist(createRegistryLock(LockAction.UNLOCK));
    entityManager.getTransaction().commit();
    Optional<Long> lockWithHighestRevisionId =
        entityManager.createQuery("from RegistryLock", RegistryLock.class).getResultList().stream()
            .sorted(Comparator.comparing(RegistryLock::getRevisionId).reversed())
            .map(RegistryLock::getRevisionId)
            .findFirst();
    entityManager.close();
    assertThat(lockWithHighestRevisionId).hasValue(entity.getRevisionId() + 1);
  }

  @Test
  public void testUpdateRegistryLock() {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    entityManager.persist(createRegistryLock(LockAction.LOCK));
    entityManager.getTransaction().commit();
    entityManager.clear();
    ;

    entityManager.getTransaction().begin();
    Optional<RegistryLock> entity =
        entityManager.createQuery("from RegistryLock order by revisionId DESC").setMaxResults(1)
            .getResultList().stream()
            .findAny();
    assertThat(entity).isPresent();
    entity.get().setLockAction(LockAction.UNLOCK);
    entityManager.getTransaction().commit();
    entityManager.close();
  }

  private static RegistryLock createRegistryLock(LockAction lockAction) {
    RegistryLock registryLock = new RegistryLock("some_repo_id");
    registryLock.setDomainName("domain.tld");
    registryLock.setLockAction(lockAction);
    registryLock.setLockStatus(LockStatus.NOT_LOCKED);
    registryLock.setRegistrarClientId("registrar_client_id");
    registryLock.setVerificationCode("blah");
    if (lockAction.equals(LockAction.LOCK)) {
      registryLock.setLockingRegistrarContactId("locking_registrar");
    } else {
      registryLock.setUnlockingRegistrarContactId("unlocking_registrar");
    }
    return registryLock;
  }
}
