package testutils;

import static org.junit.Assert.assertThrows;
import static testutils.TextSubject.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import testutils.TextSubject.DiffFormat;

/** Unit tests for {@link TextSubject}. */
@RunWith(JUnit4.class)
public class TextSubjectTest {

  private static final ImmutableList<String> EXPECTED =
      ImmutableList.of(
          "This is a random file,", "", "with three lines and terminates without a newline.");

  private static final ImmutableList<String> ACTUAL =
      ImmutableList.of("This is a random file,", "with two lines and terminates with a newline.");

  @Test
  public void contentEqual_unifiedDiff() {
    assertThat(ACTUAL).withDiffFormat(DiffFormat.UNIFIED_DIFF).contentEquals(ACTUAL);
  }

  @Test
  public void contentEqual_sideBySideDiff() {
    assertThat(ACTUAL).withDiffFormat(DiffFormat.SIDE_BY_SIDE).contentEquals(ACTUAL);
  }

  @Test
  public void contentNotEqual_unifiedDiff() {
    assertThrows(
        AssertionError.class,
        () -> assertThat(ACTUAL).withDiffFormat(DiffFormat.UNIFIED_DIFF).contentEquals(EXPECTED));
  }

  @Test
  public void contentNotEqual_sideBySideDiff() {
    assertThrows(
        AssertionError.class,
        () -> assertThat(ACTUAL).withDiffFormat(DiffFormat.SIDE_BY_SIDE).contentEquals(EXPECTED));
  }

  @Test
  public void contentDiff_unifiedDiff() {
    com.google.common.truth.Truth.assertThat(TextSubject.generateUnifiedDiff(EXPECTED, ACTUAL))
        .isEqualTo(
            "--- expected\n"
                + "+++ actual\n"
                + "@@ -2,2 +2,1 @@\n"
                + "-\n"
                + "-with three lines and terminates without a newline.\n"
                + "+with two lines and terminates with a newline.");
  }

  @Test
  public void contentDiff_sideBySideDiff() {
    com.google.common.truth.Truth.assertThat(TextSubject.generateSideBySideDiff(EXPECTED, ACTUAL))
        .isEqualTo(
            "|Expected                                              |Actual                                               |\n"
                + "|------------------------------------------------------|-----------------------------------------------------|\n"
                + "|This is a random file,                                |This is a random file,                               |\n"
                + "|                                                      |with **two** lines and terminates **with** a newline.|\n"
                + "|with ~three~ lines and terminates ~without~ a newline.|                                                     |");
  }
}
