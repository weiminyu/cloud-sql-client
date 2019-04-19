package demoschema.flywaydb;

import org.flywaydb.core.Flyway;

/** Utility method for instantiating {@link Flyway}. */
public final class FlywayUtils {

  public static Flyway createInstance(String jdbcUrl, String username, String password) {
    // TODO(weiminyu): use Hikari datasource.
    return Flyway.configure().dataSource(jdbcUrl, username, password).load();
  }

  private FlywayUtils() {}
}
