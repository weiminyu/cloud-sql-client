package demoschema.hibernate;

import static com.google.common.truth.Truth.assertThat;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

@RunWith(JUnit4.class)
public class SchemaExportsTest {

  public static final String ORM_PACKAGE = "demoschema.orm";
  public static final String GOLDEN_SCHEMA = "demo_schema.golden";

  @Rule public final TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Rule
  public final PostgreSQLContainer<?> postgreSQLContainer =
      new PostgreSQLContainer<>("postgres:9.6.12")
          .withDatabaseName("nomulus")
          .withPassword("domain-registry")
          .withUsername("postgres");

  @Before
  public void setup() {
    postgreSQLContainer.start();
  }

  @After
  public void teardown() {
    postgreSQLContainer.stop();
  }

  @Test
  public void test() throws IOException {
    File schemaFile = temporaryFolder.newFile();
    SchemaExports.generateSchema(
        postgreSQLContainer.getJdbcUrl(),
        postgreSQLContainer.getUsername(),
        postgreSQLContainer.getPassword(),
        ImmutableList.of(ORM_PACKAGE),
        schemaFile.getAbsolutePath());

    assertThat(Files.asCharSource(schemaFile, UTF_8).readLines())
        .containsExactlyElementsIn(
            Resources.asCharSource(Resources.getResource(getClass(), GOLDEN_SCHEMA), UTF_8)
                .readLines())
        .inOrder();
  }
}