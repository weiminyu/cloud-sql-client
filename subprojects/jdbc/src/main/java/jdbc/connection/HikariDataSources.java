package jdbc.connection;

import static jdbc.connection.ConnectionConfigurations.createProxyJdbcUrl;
import static jdbc.connection.ConnectionConfigurations.createSocketFactoryJdbcUrl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Map;
import javax.sql.DataSource;
import jdbc.connection.ConnectionConfigurations.DbType;

public final class HikariDataSources {

  private HikariDataSources() {}

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
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(jdbcUrl);
    config.setUsername(userName);
    config.setPassword(password);
    connectionProperties.forEach(config::addDataSourceProperty);

    // TODO(weiminyu): set additional properties.

    return new HikariDataSource(config);
  }
}
