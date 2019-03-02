package jdbc.connection;

import com.google.common.collect.ImmutableMap;

/** Utilities that provides JDBC connection properties. */
public final class ConnectionConfigurations {

  private ConnectionConfigurations() {}

  /**
   * JDBC URL Template for connection over CloudSQL socket factory.
   *
   * <p>Note that HikariCP also works correctly with {@code "jdbc:%s:///%s" }.
   */
  private static final String SOCKET_FACTORY_URL_TEMPLATE = "jdbc:%s://google/%s";

  private static final String PROXY_URL_TEMPLATE = "jdbc:%s://localhost:%s/%s";

  // Connection property keys.
  private static final String CLOUD_SQL_INSTANCE_KEY = "cloudSqlInstance";
  private static final String SOCKET_FACTORY_KEY = "socketFactory";
  private static final String MYSQL_USE_SSL_KEY = "useSSL";

  public static String createSocketFactoryJdbcUrl(DbType dbType, String dbName) {
    return String.format(SOCKET_FACTORY_URL_TEMPLATE, dbType.name, dbName);
  }

  public static String createProxyJdbcUrl(DbType dbType, String dbName, int port) {
    return String.format(PROXY_URL_TEMPLATE, dbType.name, port, dbName);
  }

  /** Collection of database-specific configurations. */
  public enum DbType {
    MYSQL("mysql", "com.google.cloud.sql.mysql.SocketFactory") {

      @Override
      public ImmutableMap<String, String> socketFactoryProperties(String cloudSqlInstance) {
        return ImmutableMap.<String, String>builder()
            .putAll(super.socketFactoryProperties(cloudSqlInstance))
            .put(MYSQL_USE_SSL_KEY, "false")
            .build();
      }

      @Override
      public ImmutableMap<String, String> proxyProperties() {
        return ImmutableMap.of(MYSQL_USE_SSL_KEY, "false");
      }
    },
    PSQL("postgresql", "com.google.cloud.sql.postgres.SocketFactory");
    private final String name;
    private final String socketFactory;

    DbType(String name, String socketFactory) {
      this.name = name;
      this.socketFactory = socketFactory;
    }

    public ImmutableMap<String, String> socketFactoryProperties(String cloudSqlInstance) {
      return ImmutableMap.of(
          SOCKET_FACTORY_KEY, socketFactory, CLOUD_SQL_INSTANCE_KEY, cloudSqlInstance);
    }

    public ImmutableMap<String, String> proxyProperties() {
      return ImmutableMap.of();
    }
  }
}
