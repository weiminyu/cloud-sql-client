package demoschema.hibernate;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.reflections.Reflections;

/**
 * Helper for native Hibernate initialization.
 *
 * <p>Creating {@link EntityManagerFactory} from {@link ServiceRegistry } is not JPA-compliant.
 * However, it is more flexible for production use. For example, it eliminates the need to store
 * JDBC URL and credentials in a configuration file. {@code ServiceRegistry} is also needed by some
 * Hibernate-specific utilities.
 */
public class ServiceRegistries {

  /**
   * Returns a {@link ServiceRegistry} for the given {@code jdbcUrl} and credentials.
   *
   * <p>Caller is encouraged to use {@code ServiceRegistry} as singleton.
   */
  public static ServiceRegistry createServiceRegistry(
      String jdbcUrl, String user, String password) {
    StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

    Map<String, Object> settings = new HashMap<>();
    // Environment.DRIVER is optional
    settings.put(Environment.URL, jdbcUrl);
    settings.put(Environment.USER, user);
    settings.put(Environment.PASS, password);
    settings.put(Environment.HBM2DDL_AUTO, "validate"); // Always use validate!
    settings.put(Environment.SHOW_SQL, true);

    settings.put(Environment.PHYSICAL_NAMING_STRATEGY, NomulusNamingStrategy.class);

    // Maximum waiting time for a connection from the pool
    settings.put("hibernate.hikari.connectionTimeout", "20000");
    // Minimum number of ideal connections in the pool
    settings.put("hibernate.hikari.minimumIdle", "10");
    // Maximum number of actual connection in the pool
    settings.put("hibernate.hikari.maximumPoolSize", "20");
    // Maximum time that a connection is allowed to sit ideal in the pool
    settings.put("hibernate.hikari.idleTimeout", "300000");

    registryBuilder.applySettings(settings);
    return registryBuilder.build();
  }

  public static Metadata getMetaData(ServiceRegistry registry, List<Class<?>> annotatedClasses) {
    MetadataSources sources = new MetadataSources(registry);
    annotatedClasses.forEach(sources::addAnnotatedClass);
    return sources.getMetadataBuilder().build();
  }

  public static Metadata getMetaDataByPackage(
      ServiceRegistry registry, List<String> packagePrefixes) {
    MetadataSources sources = new MetadataSources(registry);
    boolean honorInherited = false;
    packagePrefixes.stream()
        .map(p -> new Reflections(p).getTypesAnnotatedWith(Entity.class, honorInherited))
        .flatMap(Set::stream)
        .sorted(Comparator.comparing(Class::getCanonicalName))
        .forEach(sources::addAnnotatedClass);
    return sources.getMetadataBuilder().build();
  }

  public static EntityManagerFactory getEntityManagerFactory(Metadata metadata) {
    return metadata.buildSessionFactory();
  }
}
