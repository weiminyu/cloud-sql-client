package testutils;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertAbout;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.Patch;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.io.Resources;
import com.google.common.truth.Fact;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A {@link Subject} that checks if two text blocks are equal, and displays the diffs in an
 * easy-to-read format.
 */
public final class TextSubject extends Subject {

  private static final String ACTUAL_VALUE_ONLY_PROPERTY = "actual_only";

  private final Supplier<List<String>> actual;
  private DiffFormat diffFormat = DiffFormat.SIDE_BY_SIDE;

  protected TextSubject(FailureMetadata metadata, Supplier<List<String>> actual) {
    super(metadata, actual);
    this.actual = actual;
  }

  public TextSubject withDiffFormat(DiffFormat diffFormat) {
    this.diffFormat = diffFormat;
    return this;
  }

  public void contentEquals(File expected) throws IOException {
    checkFile(expected);
    contentEquals(Files.readAllLines(expected.toPath()));
  }

  public void contentEquals(URL resourceUrl) throws IOException {
    contentEquals(Resources.asCharSource(resourceUrl, UTF_8).readLines());
  }

  public void contentEquals(List<String> expectedContent) {
    checkNotNull(expectedContent, "expectedContent");
    List<String> actualContent = actual.get();
    if (expectedContent.equals(actualContent)) {
      return;
    }
    DiffFormat effectiveFormat =
        System.getProperty(ACTUAL_VALUE_ONLY_PROPERTY) == null
            ? diffFormat
            : DiffFormat.ACTUAL_VALUE_ONLY;
    String diffString = effectiveFormat.generateDiff(expectedContent, actualContent);
    failWithoutActual(
        Fact.simpleFact(
            Joiner.on('\n')
                .join(
                    "Files differ in content. Displaying " + effectiveFormat.name().toLowerCase(),
                    diffString)));
  }

  static String generateUnifiedDiff(List<String> expectedContent, List<String> actualContent) {
    Patch<String> diff;
    try {
      diff = DiffUtils.diff(expectedContent, actualContent);
    } catch (DiffException e) {
      throw new RuntimeException(e);
    }
    List<String> unifiedDiff =
        UnifiedDiffUtils.generateUnifiedDiff("expected", "actual", expectedContent, diff, 0);

    return Joiner.on('\n').join(unifiedDiff);
  }

  static String generateSideBySideDiff(List<String> expectedContent, List<String> actualContent) {
    DiffRowGenerator generator =
        DiffRowGenerator.create()
            .showInlineDiffs(true)
            .inlineDiffByWord(true)
            .oldTag(f -> "~")
            .newTag(f -> "**")
            .build();
    List<DiffRow> rows;
    try {
      rows = generator.generateDiffRows(expectedContent, actualContent);
    } catch (DiffException e) {
      throw new RuntimeException(e);
    }

    int maxExpectedLineLength =
        findMaxLineLength(rows.stream().map(DiffRow::getOldLine).collect(Collectors.toList()));
    int maxActualLineLength =
        findMaxLineLength(rows.stream().map(DiffRow::getNewLine).collect(Collectors.toList()));

    RowFormatter rowFormatter = new RowFormatter(maxExpectedLineLength, maxActualLineLength);

    return Joiner.on('\n')
        .join(
            rowFormatter.formatRow("Expected", "Actual", ' '),
            rowFormatter.formatRow("", "", '-'),
            rows.stream()
                .map(row -> rowFormatter.formatRow(row.getOldLine(), row.getNewLine(), ' '))
                .toArray());
  }

  private static int findMaxLineLength(Collection<String> lines) {
    return lines.stream()
        .map(String::length)
        .sorted(Comparator.reverseOrder())
        .findFirst()
        .orElse(0);
  }

  private static void checkFile(File file) {
    checkNotNull(file, "file");
    checkState(
        file.exists() && file.isFile(),
        "File %s does not exist or is not a file",
        file.getAbsolutePath());
  }

  public static TextSubject assertThat(List<String> actual) {
    return assertAbout(fileContents()).that(() -> checkNotNull(actual, "actual"));
  }

  public static TextSubject assertThat(File actual) {
    checkFile(actual);
    return assertAbout(fileContents())
        .that(
            () -> {
              try {
                return Files.readAllLines(actual.toPath());
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  private static final Subject.Factory<TextSubject, Supplier<List<String>>>
      FILE_CONTENT_SUBJECT_FILE_FACTORY = TextSubject::new;

  public static Subject.Factory<TextSubject, Supplier<List<String>>> fileContents() {
    return FILE_CONTENT_SUBJECT_FILE_FACTORY;
  }

  private static class RowFormatter {
    private final int maxExpectedLineLength;
    private final int maxActualLineLength;

    private RowFormatter(int maxExpectedLineLength, int maxActualLineLength) {
      this.maxExpectedLineLength = maxExpectedLineLength;
      this.maxActualLineLength = maxActualLineLength;
    }

    public String formatRow(String expected, String actual, char padChar) {
      return String.format(
          "|%s|%s|",
          Strings.padEnd(expected, maxExpectedLineLength, padChar),
          Strings.padEnd(actual, maxActualLineLength, padChar));
    }
  }

  public enum DiffFormat {
    UNIFIED_DIFF {
      @Override
      String generateDiff(List<String> expected, List<String> actual) {
        return generateUnifiedDiff(expected, actual);
      }
    },
    SIDE_BY_SIDE {
      @Override
      String generateDiff(List<String> expected, List<String> actual) {
        return generateSideBySideDiff(expected, actual);
      }
    },
    ACTUAL_VALUE_ONLY {
      @Override
      String generateDiff(List<String> expected, List<String> actual) {
        return Joiner.on('\n').join(actual);
      }
    };

    abstract String generateDiff(List<String> expected, List<String> actual);
  }
}
