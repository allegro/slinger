package pl.allegro.android.slinger;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.res.builder.RobolectricPackageManager;
import org.robolectric.shadows.ShadowActivity;

import java.util.ArrayList;

import pl.allegro.android.slinger.resolver.IntentResolver;
import pl.allegro.android.slinger.resolver.RedirectRule;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.spy;
import static pl.allegro.android.slinger.IntentStarterTest.Utils.preparePackageManager;

@RunWith(RobolectricGradleTestRunner.class)
public class SlingerActivityTest {

  @Test(expected = RuntimeException.class)
  public void shouldFailWithoutSpecifyingUri() {
    // given
    Intent intent = new Intent(Intent.ACTION_VIEW);

    // when
    Robolectric.buildActivity(SlingerActivity.class).withIntent(intent).create().get();
  }

  @Test
  public void shouldUri() throws PackageManager.NameNotFoundException {
    // given
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://example.com"));
    RobolectricPackageManager packageManager = (RobolectricPackageManager) preparePackageManager(intent, ImmutableList.of(Activity1.class, SlingerActivity.class));
    Activity activity = spy(Robolectric.setupActivity(Activity.class));
    activity.setIntent(intent);
    PackageInfo packageInfo = new PackageInfo();
    packageInfo.packageName = activity.getPackageName();
    ApplicationInfo applicationInfo = new ApplicationInfo();
    applicationInfo.metaData = new Bundle();
    packageInfo.applicationInfo = applicationInfo;
    packageManager.addPackage(packageInfo);
    applicationInfo.metaData.putString(ManifestParser.INTENT_RESOLVER_NAME, TestModule1.class.getName());

    // when
    SlingerActivity.startActivity(activity, intent);

    // then
    ShadowActivity shadowActivity = (ShadowActivity) ShadowExtractor.extract(activity);
    assertThat(activity.isFinishing()).isTrue();
    Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
    assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(Activity1.class);
  }


  static class Activity1 extends Activity {

  }

  public static class TestModule1 extends IntentResolver {

    public TestModule1(Activity activity) {
      super(activity);
    }

    @NonNull
    @Override
    public Iterable<RedirectRule> getRules() {
      return new ArrayList<>();
    }
  }

}
