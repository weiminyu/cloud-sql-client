package flywaydb;

import java.util.Optional;
import org.flywaydb.core.Flyway;

/** Utility method for instantiating {@link Flyway}. */
public final class FlywayUtils {

  public static Flyway createInstance(
      String jdbcUrl, String username, String password, Optional<String> sqlScriptLocation) {
    // TODO(weiminyu): use Hikari datasource.
    return Flyway.configure()
        .locations(sqlScriptLocation.orElse("flywaydb/migration"))
        .dataSource(jdbcUrl, username, password)
        .load();
  }

  private FlywayUtils() {}
}
