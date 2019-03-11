package jdbc.connection;

import static com.google.common.truth.Truth.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import javax.sql.DataSource;
import jdbc.connection.ConnectionConfigurations.DbType;
import junitparams.JUnitParamsRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Unit tests (hermetic) for JDBC connection pools using local database instances started with
 * Docker.
 */
@RunWith(JUnitParamsRunner.class)
public class ConnectionPoolTest {

  @BeforeClass
  public static void setupDatabases() {
    for (DataSourceFactory factory : DataSourceFactory.values()) {
      factory.startDockerContainer();
    }
  }

  @AfterClass
  public static void tearDownClass() {
    for (DataSourceFactory factory : DataSourceFactory.values()) {
      factory.stopDockerContainer();
    }
  }

  @Test
  @junitparams.Parameters
  public void connect_emptyDb(
      DataSourceFactory dataSourceFactory, String sql, int columnIndex, String resultPrefix)
      throws Exception {
    try (Connection conn = dataSourceFactory.get().getConnection()) {
      ResultSet resultSet = conn.createStatement().executeQuery(sql);
      assertThat(resultSet.next()).isTrue();
      assertThat(resultSet.getString(columnIndex)).startsWith(resultPrefix);
      assertThat(resultSet.next()).isFalse();
    }
  }

  @SuppressWarnings("unused")
  private Object[] parametersForConnect_emptyDb() {
    return new Object[] {
      new Object[] {DataSourceFactory.MYSQL, "show variables like 'version'", 2, "5.7.25"},
      new Object[] {DataSourceFactory.PSQL, "select version()", 1, "PostgreSQL 9.6.12"}
    };
  }

  enum DataSourceFactory {
    MYSQL(new MySQLContainer<>("mysql:5.7.25"), DbType.MYSQL),
    PSQL(new PostgreSQLContainer<>("postgres:9.6.12"), DbType.PSQL);

    private final JdbcDatabaseContainer<?> databaseContainer;
    private final DbType dbType;

    DataSourceFactory(JdbcDatabaseContainer<?> databaseContainer, DbType dbType) {
      this.databaseContainer = databaseContainer;
      this.dbType = dbType;
    }

    public void startDockerContainer() {
      databaseContainer.start();
    }

    public void stopDockerContainer() {
      databaseContainer.stop();
    }

    public DataSource get() {
      databaseContainer.start();
      return HikariDataSources.createDataSource(
          databaseContainer.getUsername(),
          databaseContainer.getPassword(),
          databaseContainer.getJdbcUrl(),
          dbType.proxyProperties());
    }
  }
}
