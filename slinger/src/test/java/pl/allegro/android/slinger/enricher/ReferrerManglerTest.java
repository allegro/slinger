package pl.allegro.android.slinger.enricher;

import android.content.Intent;
import android.net.Uri;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;

import static android.net.Uri.parse;
import static com.google.common.truth.Truth.assertThat;
import static pl.allegro.android.slinger.enricher.ReferrerMangler.addReferrerToIntent;
import static pl.allegro.android.slinger.enricher.ReferrerMangler.getReferrerUriFromIntent;

@RunWith(RobolectricGradleTestRunner.class)
public class ReferrerManglerTest {

  @Test
  public void shouldPutAndRetrieveReferrerFromIntent() {
    //given
    Intent intent = new Intent();
    Uri referrer =
        parse("android-app://com.google.android.googlequicksearchbox/https/www.google.com");
    addReferrerToIntent(intent, referrer);

    //when
    Uri referrerUriFromIntent = getReferrerUriFromIntent(intent);

    //then
    assertThat(referrerUriFromIntent).isEqualTo(referrer);
  }

  @Test
  public void shouldReturnNullForNotSetReferrer() {
    //given
    Intent intent = new Intent();
    addReferrerToIntent(intent, null);

    //when
    Uri referrerUriFromIntent = getReferrerUriFromIntent(intent);

    //then
    assertThat(referrerUriFromIntent).isNull();
  }
}