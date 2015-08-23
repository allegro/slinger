package pl.allegro.android.slinger.enricher;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;

import static android.net.Uri.parse;
import static pl.allegro.android.slinger.enricher.ReferrerMangler.EXTRA_REFERRER;
import static pl.allegro.android.slinger.enricher.ReferrerMangler.EXTRA_REFERRER_NAME;
import static pl.allegro.android.slinger.enricher.ReferrerMangler.getReferrerUriFromIntent;
import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricGradleTestRunner.class) public class DefaultIntentEnricherTest {

  private DefaultIntentEnricher objectUnderTest = new DefaultIntentEnricher();

  @Test public void shouldEnrichIntentWithReferrerAndOriginatingUri() {
    //given
    Activity activity = new Activity();
    Uri referrerUri = parse("android-app://some.referrer/foo/bar");
    activity.setIntent(new Intent().putExtra(EXTRA_REFERRER, referrerUri));
    Uri uriThatStartedActivity = parse("http://www.example.com");

    Intent intentToEnrich = new Intent();

    //when
    objectUnderTest.enrichSlingedIntent(activity, uriThatStartedActivity, intentToEnrich);

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
    objectUnderTest.enrichSlingedIntent(activity, uriThatStartedActivity, intentToEnrich);

    //then
    assertThat(intentToEnrich.getData()).isEqualTo(uriThatStartedActivity);
    assertThat(getReferrerUriFromIntent(intentToEnrich)).isEqualTo(parse(referrerString));
  }
}