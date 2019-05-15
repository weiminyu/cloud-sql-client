package demoschema.hibernate;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import demoschema.orm.DomainEntity;
import java.util.Comparator;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.service.ServiceRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Integration tests using Hibernate and a real database. */
@RunWith(JUnit4.class)
public class HibernateIntegrationTest {

  @Test
  public void test() throws Exception {
    ServiceRegistry serviceRegistry =
        ServiceRegistries.createServiceRegistry(
            checkNotNull(System.getProperty("jdbcUrl"), "jdbcUrl"),
            checkNotNull(System.getProperty("dbUser"), "dbUser"),
            checkNotNull(System.getProperty("dbPassword", "dbPassword")));
    Metadata metadata =
        ServiceRegistries.getMetaDataByPackage(serviceRegistry, ImmutableList.of("demoschema.orm"));
    EntityManagerFactory entityManagerFactory = ServiceRegistries.getEntityManagerFactory(metadata);
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    Optional<String> lastEppRepoId =
        entityManager.createQuery("from DomainEntity", DomainEntity.class).getResultList().stream()
            .sorted(Comparator.comparing(DomainEntity::getEppRepoId).reversed())
            .findFirst()
            .map(DomainEntity::getEppRepoId);
    DomainEntity entity = new DomainEntity();
    entity.setEppRepoId(lastEppRepoId.orElse("") + "1");
    entityManager.persist(entity);
    entityManager.getTransaction().commit();
    entityManager.close();
  }
}
