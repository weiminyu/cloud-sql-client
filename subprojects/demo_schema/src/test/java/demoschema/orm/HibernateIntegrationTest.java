package demoschema.orm;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import java.util.Comparator;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
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
    // Auto-increment column in composite key is not supported in Hibernate 5.4.
    // The persist call will fail.
    RegistryLock entity = new RegistryLock(1L, null);
    //entityManager.persist(entity);
    entityManager.getTransaction().commit();
    Optional<RegistryLockId> lockWithHighestRevisionId =
        entityManager.createQuery("from RegistryLock", RegistryLock.class).getResultList().stream()
            .map(RegistryLock::getId)
            .sorted(Comparator.comparing(RegistryLockId::getRevisionId).reversed())
            .findFirst();
    entityManager.close();

  }
}
