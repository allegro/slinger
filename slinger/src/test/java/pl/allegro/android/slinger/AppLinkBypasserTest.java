package pl.allegro.android.slinger;

import android.content.Intent;
import android.net.Uri;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;

import static android.net.Uri.parse;
import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricGradleTestRunner.class) public class AppLinkBypasserTest {

  private AppLinkBypasser objectUnderTest =
      new AppLinkBypasser(RuntimeEnvironment.application.getPackageManager());

  @Test public void uriShouldContainOnlyScheme() {
    // given
    Uri uri = parse("http://example.com");

    // when
    Uri rawSchemeUri = objectUnderTest.getRawSchemeUri(uri);

    // then
    assertThatUriContainsOnlyScheme(rawSchemeUri, "http");
  }

  @Test public void intentShouldContainOnlyUriWithScheme() {
    // given
    Intent intent = new Intent().setData(parse("http://example.com"));

    // when
    Intent rawSchemeIntent = objectUnderTest.getIntentWithRawSchemeUri(intent);

    // then
    assertThat(rawSchemeIntent.getScheme()).isEqualTo(intent.getScheme());
    assertThatUriContainsOnlyScheme(rawSchemeIntent.getData(), "http");
    assertThat(rawSchemeIntent.getAction()).isEqualTo(intent.getAction());
    assertThat(rawSchemeIntent.getCategories()).isEqualTo(intent.getCategories());
  }

  private void assertThatUriContainsOnlyScheme(Uri rawSchemeUri, String scheme) {
    assertThat(rawSchemeUri.getScheme()).isEqualTo(scheme);
    assertThat(rawSchemeUri.getAuthority()).isNull();
    assertThat(rawSchemeUri.getHost()).isNull();
    assertThat(rawSchemeUri.getPort()).isEqualTo(-1);
    assertThat(rawSchemeUri.getPath()).isEmpty();
    assertThat(rawSchemeUri.getQuery()).isNull();
  }
}
