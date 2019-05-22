package liquibase;

import java.sql.DriverManager;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.testcontainers.containers.PostgreSQLContainer;

/** Unit tests for using {@link Liquibase}. */
@RunWith(JUnit4.class)
public class LiquibaseTest {

  @ClassRule
  public static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER =
      new PostgreSQLContainer<>("postgres:9.6.12")
          .withDatabaseName("anything-goes")
          .withUsername("whomever")
          .withPassword("doesn't-matter");

  private Liquibase liquibase;

  @Before
  public void setup() throws Exception {
    liquibase =
        LiquibaseUtils.createLiquibase(
            DriverManager.getConnection(
                POSTGRE_SQL_CONTAINER.getJdbcUrl(),
                POSTGRE_SQL_CONTAINER.getUsername(),
                POSTGRE_SQL_CONTAINER.getPassword()),
            "testschemas/xml/ChangeLog.xml");
  }

  @Test
  public void validateChangeLog_success() throws Exception {
    liquibase.validate();
  }

  @Test
  public void checkDatabaseStatus_notUpToDate() {
    // TODO
  }

  @Test
  public void updateDatabase_success() throws Exception {
    liquibase.update("");
    // TODO: verify updated schema.
  }
}
