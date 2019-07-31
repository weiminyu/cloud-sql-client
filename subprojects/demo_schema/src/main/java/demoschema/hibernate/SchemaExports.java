package demoschema.hibernate;

import com.google.common.base.Joiner;
import java.util.EnumSet;
import java.util.List;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaExport.Action;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.schema.TargetType;

/** Utility methods that export full or incremental schema to a file. */
public class SchemaExports {

  public static void exportFullSchema(
      String jdbcUrl,
      String user,
      String password,
      List<String> importFilePath,
      List<String> ormPackagePrefixes,
      String schemaFile) {
    try (AutoCloseServiceRegistry registry =
        new AutoCloseServiceRegistry(jdbcUrl, user, password)) {
      Metadata metadata = registry.getMetadata(ormPackagePrefixes);
      SchemaExport schemaExport = new SchemaExport();
      schemaExport.setOutputFile(schemaFile);
      schemaExport.setImportFiles(Joiner.on(',').join(importFilePath));
      schemaExport.setFormat(true);
      schemaExport.setDelimiter(";");
      schemaExport.execute(EnumSet.of(TargetType.SCRIPT), Action.CREATE, metadata);
    }
  }

  /**
   * Exports the full schema creation script from all ORM entity classes found in {@code
   * ormPackagePrefixes} and writes it to {@code schemaFile}.
   *
   * <p>This method instantiates a {@link ServiceRegistry}, which requires a live database instance.
   */
  public static void exportFullSchema(
      String jdbcUrl,
      String user,
      String password,
      List<String> ormPackagePrefixes,
      String schemaFile) {
    try (AutoCloseServiceRegistry registry =
        new AutoCloseServiceRegistry(jdbcUrl, user, password)) {
      Metadata metadata = registry.getMetadata(ormPackagePrefixes);
      SchemaExport schemaExport = new SchemaExport();
      schemaExport.setOutputFile(schemaFile);
      schemaExport.setFormat(true);
      schemaExport.setDelimiter(";");
      schemaExport.execute(EnumSet.of(TargetType.SCRIPT), Action.CREATE, metadata);
    }
  }

  /**
   * Exports the sql script that could update the schema in the connected database instance to the
   * target schema as defined by all ORM entity classes found in {@code ormPackagePrefixes}.
   */
  public static void exportSchemaUpdate(
      String jdbcUrl,
      String user,
      String password,
      List<String> ormPackagePrefixes,
      String schemaFile) {
    try (AutoCloseServiceRegistry registry =
        new AutoCloseServiceRegistry(jdbcUrl, user, password)) {
      Metadata metadata = registry.getMetadata(ormPackagePrefixes);
      SchemaUpdate schemaUpdate = new SchemaUpdate();
      schemaUpdate.setOutputFile(schemaFile);
      schemaUpdate.setFormat(true);
      schemaUpdate.setDelimiter(";");
      schemaUpdate.execute(EnumSet.of(TargetType.SCRIPT), metadata);
    }
  }

  private static class AutoCloseServiceRegistry implements AutoCloseable {
    private final ServiceRegistry serviceRegistry;

    private AutoCloseServiceRegistry(String jdbcUrl, String user, String password) {
      this.serviceRegistry = ServiceRegistries.createServiceRegistry(jdbcUrl, user, password);
    }

    public Metadata getMetadata(List<String> ormPackagePrefixes) {
      return ServiceRegistries.getMetaDataByPackage(serviceRegistry, ormPackagePrefixes);
    }

    @Override
    public void close() {
      StandardServiceRegistryBuilder.destroy(serviceRegistry);
    }
  }
}
