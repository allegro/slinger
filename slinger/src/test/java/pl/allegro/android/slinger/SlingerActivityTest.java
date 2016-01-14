package pl.allegro.android.slinger;

import android.app.Activity;
import android.content.Intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
public class SlingerActivityTest {

  @Test
  public void shouldFinishIfUriIsNull() {
    // given
    Intent intent = new Intent(Intent.ACTION_VIEW);

    // when
    Activity activity = Robolectric.buildActivity(SlingerActivity.class).withIntent(intent).create().get();

    // then
    assertThat(activity.isFinishing()).isTrue();
  }
}
