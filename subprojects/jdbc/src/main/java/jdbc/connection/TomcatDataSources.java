package jdbc.connection;

import static jdbc.connection.ConnectionConfigurations.createProxyJdbcUrl;
import static jdbc.connection.ConnectionConfigurations.createSocketFactoryJdbcUrl;

import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import jdbc.connection.ConnectionConfigurations.DbType;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/** Helper methods for creating JDBC connection pools using tomcat-jdbc. */
public final class TomcatDataSources {

  private TomcatDataSources() {}

  public static DataSource createSocketFactoryDataSource(
      DbType dbType, String userName, String password, String dbName, String cloudSqlInstanceName) {
    return createDataSource(
        userName,
        password,
        createSocketFactoryJdbcUrl(dbType, dbName),
        dbType.socketFactoryProperties(cloudSqlInstanceName));
  }

  public static DataSource createProxyDataSource(
      DbType dbType, String userName, String password, String dbName, int port) {
    return createDataSource(
        userName, password, createProxyJdbcUrl(dbType, dbName, port), dbType.proxyProperties());
  }

  private static DataSource createDataSource(
      String userName, String password, String jdbcUrl, Map<String, String> connectionProperties) {
    PoolProperties properties = new PoolProperties();
    properties.setUrl(jdbcUrl);
    properties.setUsername(userName);
    properties.setPassword(password);
    properties.setConnectionProperties(
        connectionProperties.entrySet().stream()
            .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(";")));

    // TODO(weiminyu): set additional properties.

    org.apache.tomcat.jdbc.pool.DataSource dataSource =
        new org.apache.tomcat.jdbc.pool.DataSource();
    dataSource.setPoolProperties(properties);
    return dataSource;
  }
}
