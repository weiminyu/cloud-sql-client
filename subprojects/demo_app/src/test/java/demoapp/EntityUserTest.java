package demoapp;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class EntityUserTest {

  @Test
  public void test() {
    assertThat(EntityUser.LOCK.getCreationTimestamp()).isNull();
    System.out.println("Test done");
  }
}
