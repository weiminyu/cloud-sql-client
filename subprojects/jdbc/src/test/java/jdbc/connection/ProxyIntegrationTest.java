package jdbc.connection;

import static com.google.common.truth.Truth.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import javax.sql.DataSource;
import jdbc.connection.ConnectionConfigurations.DbType;
import jdbc.connection.IntegrationTestDataLoader.DataSourceFactory;
import jdbc.connection.IntegrationTestDataLoader.TestDb;
import junitparams.JUnitParamsRunner;
import junitparams.naming.TestCaseName;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Integration tests for CloudSQL connections over proxy.
 *
 * <p>CloudSQL proxies for both MYSQL and POSTGRESQL must be started before these tests can run.
 */
@RunWith(JUnitParamsRunner.class)
public class ProxyIntegrationTest {

  @Test
  @junitparams.Parameters(source = IntegrationTestDataLoader.class)
  @TestCaseName("{0}-{1}")
  public void getDbVersion(DataSourceFactory dataSourceFactory, DbType dbType, TestDb dbDetails)
      throws Exception {
    DataSource dataSource = dataSourceFactory.getProxyDataSource(dbType, dbDetails);
    try (Connection conn = dataSource.getConnection();
        ResultSet resultSet = conn.createStatement().executeQuery(dbDetails.versionQuery)) {
      assertThat(resultSet.next()).isTrue();
      assertThat(resultSet.getString(dbDetails.resultColumnIndex))
          .startsWith(dbDetails.resultPrefix);
      assertThat(resultSet.next()).isFalse();
    }
  }
}
