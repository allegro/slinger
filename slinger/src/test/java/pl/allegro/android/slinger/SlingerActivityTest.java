package pl.allegro.android.slinger;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;

import java.util.List;

import pl.allegro.android.slinger.resolver.IntentResolver;
import pl.allegro.android.slinger.resolver.RedirectRule;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static pl.allegro.android.slinger.IntentStarterTest.Utils.preparePackageManager;
import static pl.allegro.android.slinger.resolver.RedirectRule.builder;

@RunWith(RobolectricGradleTestRunner.class)
public class SlingerActivityTest {

  @Test(expected = RuntimeException.class)
  public void shouldFailWithoutSpecifyingUri() {
    // given
    Intent intent = new Intent(Intent.ACTION_VIEW);

    // when
    Robolectric.buildActivity(SlingerActivity.class).withIntent(intent).create().get();
  }

  @Test(expected = RuntimeException.class)
  public void shouldFailForNullIntent() {
    Robolectric.buildActivity(SlingerActivity.class).withIntent(null).create().get();
  }

  @Test public void shouldStartActivityWithUri() throws PackageManager.NameNotFoundException {
    // given
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://example.com"));
    Activity activity = spy(Robolectric.setupActivity(Activity.class));
    activity.setIntent(intent);

    preparePackageManager(new Intent(intent).setClass(activity, Activity1.class),ImmutableList.<Class<? extends Activity>>of(Activity1.class));
    preparePackageManager(new Intent(intent).setClass(activity, SlingerActivity.class), ImmutableList.<Class<? extends Activity>>of(SlingerActivity.class));
    new PackageManagerPreparator().getActivityInfo()
        .metaData.putString(ManifestParser.INTENT_RESOLVER_NAME, TestModule1.class.getName());

    // when
    SlingerActivity.startActivity(activity, intent);

    // then
    verify(activity).startActivity(any(Intent.class));
  }

  static class Activity1 extends Activity {

  }

  public static class TestModule1 extends IntentResolver {

    public static final String PATTERN_FOR_EXAMPLE_HOST = "http(s)?://example.com.*";

    private List<RedirectRule> rules;

    public TestModule1(Activity activity) {
      super(activity);
      rules = ImmutableList.of(getRedirectRuleForExampleHost(activity));
    }

    RedirectRule getRedirectRuleForExampleHost(Activity activity) {
      return builder().intent(new Intent(activity, Activity1.class).setAction(Intent.ACTION_VIEW)).pattern(
          PATTERN_FOR_EXAMPLE_HOST).build();
    }

    @NonNull
    @Override
    public Iterable<RedirectRule> getRules() {
      return rules;
    }
  }
}