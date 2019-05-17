package flywaydb;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import java.util.Optional;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
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
    flyway =
        FlywayUtils.createInstance(
            POSTGRE_SQL_CONTAINER.getJdbcUrl(),
            POSTGRE_SQL_CONTAINER.getUsername(),
            POSTGRE_SQL_CONTAINER.getPassword(),
            Optional.of("flywaydb/migration"));
  }

  @Test
  public void initial_schema_success() {
    assertThrows(FlywayException.class, flyway::validate);
    assertThat(flyway.migrate()).isEqualTo(1);
    flyway.validate();
  }
}
