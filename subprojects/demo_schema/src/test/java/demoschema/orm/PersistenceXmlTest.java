package demoschema.orm;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.EntityType;
import org.hibernate.cfg.Environment;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.reflections.Reflections;
import org.testcontainers.containers.PostgreSQLContainer;

@RunWith(JUnit4.class)
public class PersistenceXmlTest {

  @ClassRule
  public static PostgreSQLContainer postgreSQLContainer =
      new PostgreSQLContainer<>("postgres:9.6.12")
          .withInitScript("demoschema/hibernate/demo_schema.golden");

  static EntityManagerFactory entityManagerFactory;

  @BeforeClass
  public static void setup() {
    entityManagerFactory =
        Persistence.createEntityManagerFactory(
            "nomulus",
            ImmutableMap.of(
                Environment.JPA_JDBC_URL, postgreSQLContainer.getJdbcUrl(),
                Environment.JPA_JDBC_USER, postgreSQLContainer.getUsername(),
                Environment.JPA_JDBC_PASSWORD, postgreSQLContainer.getPassword(),
                Environment.SHOW_SQL, "true"));
  }

  @Test
  public void allEntityClassesConfigured() {
    boolean honorInherited = true; // Allows inheritance of the @Entity annotation.

    assertThat(
            entityManagerFactory.getMetamodel().getEntities().stream()
                .map(EntityType::getJavaType)
                .collect(Collectors.toSet()))
        .containsAtLeastElementsIn(
            new Reflections("demoschema.orm")
                .getTypesAnnotatedWith(Entity.class, honorInherited).stream()
                    .collect(Collectors.toSet()));
  }
}
