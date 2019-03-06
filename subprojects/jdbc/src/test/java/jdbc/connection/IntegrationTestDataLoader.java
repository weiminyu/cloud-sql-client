package jdbc.connection;

import com.google.common.io.Resources;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.sql.DataSource;
import jdbc.connection.ConnectionConfigurations.DbType;
import org.yaml.snakeyaml.Yaml;

/** Utilities for loading details about CloudSQL database instances for integration tests. */
public class IntegrationTestDataLoader {

  private static final String DETAILS_RESOURCES = "db_instances.yaml";
  static final TestConfig TEST_CONFIG = loadTestConfigs();

  private IntegrationTestDataLoader() {}

  @SuppressWarnings("unchecked")
  private static TestConfig loadTestConfigs() {
    try {
      return new Yaml()
          .loadAs(
              Resources.asCharSource(
                      IntegrationTestDataLoader.class.getResource(DETAILS_RESOURCES),
                      StandardCharsets.UTF_8)
                  .openBufferedStream(),
              TestConfig.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Provides test data for parameterized integration tests in this package. */
  public static Object[] provideTestDbConfigs() {
    return new Object[] {
      new Object[] {DataSourceFactory.HIKARI, DbType.PSQL, TEST_CONFIG.psql},
      new Object[] {DataSourceFactory.HIKARI, DbType.MYSQL, TEST_CONFIG.mysql},
      new Object[] {DataSourceFactory.TOMCAT, DbType.PSQL, TEST_CONFIG.psql},
      new Object[] {DataSourceFactory.TOMCAT, DbType.MYSQL, TEST_CONFIG.mysql}
    };
  }

  /** Helper interface for parameterized tests. */
  interface ProxyDataSourceFactory {
    DataSource create(DbType dbType, String userName, String password, String dbName, int port);
  }

  /** Helper interface for parameterized tests. */
  interface SocketFactoryDataSourceFactory {
    DataSource create(
        DbType dbType, String userName, String password, String dbName, String sqlInstance);
  }

  enum DataSourceFactory {
    HIKARI {
      @Override
      ProxyDataSourceFactory getProxyDataSourceFactory() {
        return HikariDataSources::createProxyDataSource;
      }

      @Override
      SocketFactoryDataSourceFactory getSocketFactoryDataSourceFactory() {
        return HikariDataSources::createSocketFactoryDataSource;
      }
    },
    TOMCAT {
      @Override
      ProxyDataSourceFactory getProxyDataSourceFactory() {
        return TomcatDataSources::createProxyDataSource;
      }

      @Override
      SocketFactoryDataSourceFactory getSocketFactoryDataSourceFactory() {
        return TomcatDataSources::createSocketFactoryDataSource;
      }
    };

    abstract ProxyDataSourceFactory getProxyDataSourceFactory();

    abstract SocketFactoryDataSourceFactory getSocketFactoryDataSourceFactory();

    DataSource getProxyDataSource(DbType dbType, TestDb testDb) {
      return getProxyDataSourceFactory()
          .create(dbType, testDb.userName, testDb.password, testDb.dbName, testDb.port);
    }

    DataSource getSocketFactoryDataSource(DbType dbType, TestDb testDb) {
      return getSocketFactoryDataSourceFactory()
          .create(dbType, testDb.userName, testDb.password, testDb.dbName, testDb.sqlInstanceName);
    }
  }

  /** Hold of deserialized test data from db_instances.yaml resource. */
  static class TestDb {
    public String userName;
    public String password;
    public String dbName;
    public int port;
    public String sqlInstanceName;
    public String versionQuery;
    public int resultColumnIndex;
    public String resultPrefix;
  }

  /** Hold of deserialized test data from db_instances.yaml resource. */
  static class TestConfig {
    public TestDb mysql;
    public TestDb psql;
  }
}
