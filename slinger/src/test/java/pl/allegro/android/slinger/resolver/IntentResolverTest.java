package pl.allegro.android.slinger.resolver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import pl.allegro.android.slinger.ReferrerMangler;

import static android.content.Intent.ACTION_VIEW;
import static android.net.Uri.EMPTY;
import static android.net.Uri.parse;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.robolectric.RuntimeEnvironment.application;
import static pl.allegro.android.slinger.ReferrerMangler.EXTRA_REFERRER_NAME;
import static pl.allegro.android.slinger.ReferrerMangler.getReferrerUriFromIntent;
import static pl.allegro.android.slinger.resolver.RedirectRule.builder;

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

  private IntentResolver objectUnderTest = new IntentResolver(getActivity()) {
    @NonNull @Override public Iterable<RedirectRule> getRules() {
      return asList(RULE_A, RULE_B);
    }
  };

  private ActivityA getActivity() {
    return Robolectric.buildActivity(ActivityA.class).create().get();
  }

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

  @Test public void shouldEnrichIntentWithReferrerAndOriginatingUri() {
    //given
    Activity activity = new Activity();
    Uri referrerUri = parse("android-app://some.referrer/foo/bar");
    activity.setIntent(new Intent().putExtra(ReferrerMangler.EXTRA_REFERRER, referrerUri));
    Uri uriThatStartedActivity = parse("http://www.example.com");

    Intent intentToEnrich = new Intent();

    //when
    objectUnderTest.enrichIntent(activity, intentToEnrich, uriThatStartedActivity);

    //then
    assertThat(intentToEnrich.getData()).isEqualTo(uriThatStartedActivity);
    assertThat(getReferrerUriFromIntent(intentToEnrich)).isEqualTo(referrerUri);
  }

  @Test public void shouldEnrichIntentWithReferrerStringAndOriginatingUri() {
    //given
    Activity activity = new Activity();
    String referrerString = "android-app://some.referrer/foo/bar";
    activity.setIntent(new Intent().putExtra(EXTRA_REFERRER_NAME, referrerString));
    Uri uriThatStartedActivity = parse("http://www.example.com");

    Intent intentToEnrich = new Intent();

    //when
    objectUnderTest.enrichIntent(activity, intentToEnrich, uriThatStartedActivity);

    //then
    assertThat(intentToEnrich.getData()).isEqualTo(uriThatStartedActivity);
    assertThat(getReferrerUriFromIntent(intentToEnrich)).isEqualTo(parse(referrerString));
  }

  private static Context getApplicationContext() {
    return application.getApplicationContext();
  }

  static class ActivityA extends Activity {
  }

  static class ActivityB extends Activity {
  }
}