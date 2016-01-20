package pl.allegro.android.slinger;

import android.content.Intent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;

@RunWith(RobolectricGradleTestRunner.class) public class SlingerActivityTest {
  @Test(expected = RuntimeException.class) public void shouldFailWithoutSpecifyingUri() {
    // given
    Intent intent = new Intent(Intent.ACTION_VIEW);

    // when
    Robolectric.buildActivity(SlingerActivity.class).withIntent(intent).create().get();
  }

  @Test(expected = RuntimeException.class) public void shouldFailForNullIntent() {
    Robolectric.buildActivity(SlingerActivity.class).withIntent(null).create().get();
  }
}
