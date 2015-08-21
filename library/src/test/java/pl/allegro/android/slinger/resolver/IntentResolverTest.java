package pl.allegro.android.slinger.resolver;

import android.content.Context;
import android.content.Intent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;

import static android.content.Intent.ACTION_VIEW;
import static android.net.Uri.EMPTY;
import static android.net.Uri.parse;
import static pl.allegro.android.slinger.resolver.RedirectRule.builder;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.robolectric.RuntimeEnvironment.application;

@RunWith(RobolectricGradleTestRunner.class) public class IntentResolverTest {

  public static final String PATTERN_FOR_A = "http://example.com/abc\\.html\\?query=a.*";
  public static final String PATTERN_FOR_B = "http://example.com/abc\\.html\\?query=b.*";

  public static final RedirectRule RULE_A =
      builder().intent(new Intent(getApplicationContext(), ActivityA.class))
          .pattern(PATTERN_FOR_A)
          .build();

  public static final RedirectRule RULE_B =
      builder().intent(new Intent(getApplicationContext(), ActivityB.class))
          .pattern(PATTERN_FOR_B)
          .build();

  private IntentResolver objectUnderTest = new IntentResolver(asList(RULE_A, RULE_B));

  @Test public void shouldReturnDefaultIntent() {

    //when
    Intent result = objectUnderTest.resolveIntentToSling(EMPTY);

    //then
    assertThat(result.getAction()).isEqualTo(ACTION_VIEW);
  }

  @Test public void shouldReturnIntentForA() {

    //when
    Intent result =
        objectUnderTest.resolveIntentToSling(parse("http://example.com/abc.html?query=abb"));

    //then
    assertThat(result.getComponent().getClassName()).isEqualTo(ActivityA.class.getName());
  }

  @Test public void shouldReturnIntentForB() {

    //when
    Intent result =
        objectUnderTest.resolveIntentToSling(parse("http://example.com/abc.html?query=baa"));

    //then
    assertThat(result.getComponent().getClassName()).isEqualTo(ActivityB.class.getName());
  }

  private static Context getApplicationContext() {
    return application.getApplicationContext();
  }

  private static class ActivityA {
  }

  private static class ActivityB {
  }
}