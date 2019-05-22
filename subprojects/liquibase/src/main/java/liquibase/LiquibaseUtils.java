package liquibase;

import java.sql.Connection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

/** Holds utility method that creates {@link Liquibase} instances. */
public final class LiquibaseUtils {

  /**
   * Creates a {@link Liquibase} instance based on a live database {@code connection} and a {@code
   * changeLogPath}.
   *
   * @param changeLogPath resource or file system path to the ChangeLog file
   */
  public static Liquibase createLiquibase(Connection connection, String changeLogPath)
      throws DatabaseException {

    return new Liquibase(
        changeLogPath,
        new ClassLoaderResourceAccessor(),
        DatabaseFactory.getInstance()
            .findCorrectDatabaseImplementation(new JdbcConnection(connection)));
  }

  private LiquibaseUtils() {}
}
