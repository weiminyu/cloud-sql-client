package jdbc.connection;

import static com.google.common.truth.Truth.assertThat;
import static jdbc.connection.ConnectionConfigurations.DbType.MYSQL;
import static jdbc.connection.ConnectionConfigurations.DbType.PSQL;
import static jdbc.connection.ConnectionConfigurations.createProxyJdbcUrl;
import static jdbc.connection.ConnectionConfigurations.createSocketFactoryJdbcUrl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link ConnectionConfigurations}. */
@RunWith(JUnit4.class)
public class ConnectionConfigurationsTest {

  @Test
  public void createSocketFactoryJdbcUrl_mysql() {
    assertThat(createSocketFactoryJdbcUrl(MYSQL, "dbName")).isEqualTo("jdbc:mysql://google/dbName");
  }

  @Test
  public void createSocketFactoryJdbcUrl_psql() {
    assertThat(createSocketFactoryJdbcUrl(PSQL, "dbName"))
        .isEqualTo("jdbc:postgresql://google/dbName");
  }

  @Test
  public void createProxyJdbcUrl_mysql() {
    assertThat(createProxyJdbcUrl(MYSQL, "dbName", 1234))
        .isEqualTo("jdbc:mysql://localhost:1234/dbName");
  }

  @Test
  public void createProxyJdbcUrl_psql() {
    assertThat(createProxyJdbcUrl(PSQL, "dbName", 1234))
        .isEqualTo("jdbc:postgresql://localhost:1234/dbName");
  }

  @Test
  public void mysqlSocketFactoryProperties() {
    assertThat(MYSQL.socketFactoryProperties("sql_instance"))
        .containsExactly(
            "cloudSqlInstance",
            "sql_instance",
            "socketFactory",
            "com.google.cloud.sql.mysql.SocketFactory",
            "useSSL",
            "false");
  }

  @Test
  public void psqlSocketFactoryProperties() {
    assertThat(PSQL.socketFactoryProperties("sql_instance"))
        .containsExactly(
            "cloudSqlInstance",
            "sql_instance",
            "socketFactory",
            "com.google.cloud.sql.postgres.SocketFactory");
  }

  @Test
  public void mysqlProxyProperties() {
    assertThat(MYSQL.proxyProperties()).containsExactly("useSSL", "false");
  }

  @Test
  public void psqlProxyProperties() {
    assertThat(PSQL.proxyProperties()).isEmpty();
  }
}
