package flywaydb;

import static com.google.common.truth.Truth.assertThat;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertThrows;

import com.google.common.io.Resources;
import java.util.Optional;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.PostgreSQLContainer;

/** Unit tests for schema migration using {@link Flyway}. */
@RunWith(JUnit4.class)
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class FlywayTest {

  private static final String PG_DUMP_COMMAND_PATTERN =
      "pg_dump -h localhost -U %s --schema-only --no-owner --no-privileges  "
          + "--exclude-table flyway_schema_history %s";

  @ClassRule
  public static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER =
      new PostgreSQLContainer<>("postgres:9.6.12");

  private Flyway flyway;

  @Before
  public void setup() {
    flyway =
        FlywayUtils.createInstance(
            POSTGRE_SQL_CONTAINER.getJdbcUrl(),
            POSTGRE_SQL_CONTAINER.getUsername(),
            POSTGRE_SQL_CONTAINER.getPassword(),
            Optional.of("schema/migration"));
  }

  @Test
  public void initial_schema_success() {
    assertThrows(FlywayException.class, flyway::validate);
    assertThat(flyway.migrate()).isEqualTo(1);
    flyway.validate();
  }

  @Test
  public void schema_compare_success() throws Exception {
    Container.ExecResult execResult =
        POSTGRE_SQL_CONTAINER.execInContainer(
            UTF_8,
            String.format(
                    PG_DUMP_COMMAND_PATTERN,
                    POSTGRE_SQL_CONTAINER.getUsername(),
                    POSTGRE_SQL_CONTAINER.getDatabaseName())
                .split("\\s+"));
    if (execResult.getExitCode() != 0) {
      throw new RuntimeException(execResult.toString());
    }
    assertThat(execResult.getStdout())
        .isEqualTo(
            Resources.toString(
                Resources.getResource("schema/expected/initial_schema_dump.sql"), UTF_8));
  }
}
