package demoschema.hibernate;

import java.util.EnumSet;
import java.util.List;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaExport.Action;
import org.hibernate.tool.schema.TargetType;

/** Holder of utility method to export a full schema to a file. */
public class SchemaExports {

  /**
   * Generates the full schema creation script from all ORM entity classes found in {@code
   * ormPackagePrefixes} and writes it to {@code schemaFile}.
   *
   * <p>This method calls Hibernate's {@link SchemaExport}, which requires a live database instance.
   */
  public static void generateSchema(
      String jdbcUrl,
      String user,
      String password,
      List<String> ormPackagePrefixes,
      String schemaFile) {
    ServiceRegistry serviceRegistry =
        ServiceRegistries.createServiceRegistry(jdbcUrl, user, password);
    Metadata metadata = ServiceRegistries.getMetaDataByPackage(serviceRegistry, ormPackagePrefixes);
    SchemaExport schemaExport = new SchemaExport();
    schemaExport.setOutputFile(schemaFile);
    schemaExport.execute(EnumSet.of(TargetType.SCRIPT), Action.CREATE, metadata, serviceRegistry);
    StandardServiceRegistryBuilder.destroy(serviceRegistry);
  }
}
