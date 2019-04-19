package demoschema.flywaydb;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.flywaydb.core.api.configuration.Configuration;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.testcontainers.containers.PostgreSQLContainer;

/** Unit tests for schema migration using {@link Flyway}. */
@RunWith(JUnit4.class)
public class FlywayTest {

  @ClassRule
  public static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER =
      new PostgreSQLContainer<>("postgres:9.6.12")
          .withDatabaseName("anything-goes")
          .withUsername("whomever")
          .withPassword("doesn't-matter");

  private Flyway flyway;

  @Before
  public void setup() {
    Configuration configuration =
        Flyway.configure()
            .dataSource(
                POSTGRE_SQL_CONTAINER.getJdbcUrl(),
                POSTGRE_SQL_CONTAINER.getUsername(),
                POSTGRE_SQL_CONTAINER.getPassword());
    ClassicConfiguration testConfig = new ClassicConfiguration(configuration);
    testConfig.setLocationsAsStrings("demoschema/flywaydb/migration");
    flyway = new Flyway(testConfig);
  }

  @Test
  public void initial_schema_success() {
    assertThrows(FlywayException.class, flyway::validate);
    assertThat(flyway.migrate()).isEqualTo(1);
    flyway.validate();
  }
}
