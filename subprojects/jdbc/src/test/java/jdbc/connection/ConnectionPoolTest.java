package jdbc.connection;

import static com.google.common.truth.Truth.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import javax.sql.DataSource;
import jdbc.connection.ConnectionConfigurations.DbType;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.testcontainers.containers.PostgreSQLContainer;

@RunWith(JUnit4.class)
public class ConnectionPoolTest {

  @ClassRule
  public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:9.6");

  private static DataSource dataSource;

  @BeforeClass
  public static void setupJdbcDataSource() {
    postgreSQLContainer.start();
    dataSource =
        HikariDataSources.createDataSource(
            postgreSQLContainer.getUsername(),
            postgreSQLContainer.getPassword(),
            postgreSQLContainer.getJdbcUrl(),
            DbType.PSQL.proxyProperties());
  }

  @Test
  public void dummy() throws Exception {
    try (Connection conn = dataSource.getConnection()) {
      ResultSet resultSet = conn.createStatement().executeQuery("select version()");
      assertThat(resultSet.next()).isTrue();
      assertThat(resultSet.getString(1)).startsWith("PostgreSQL 9.6");
      assertThat(resultSet.next()).isFalse();
    }
  }
}
