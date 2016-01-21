package pl.allegro.android.slinger;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import pl.allegro.android.slinger.enricher.DefaultIntentEnricher;
import pl.allegro.android.slinger.enricher.IntentEnricher;
import pl.allegro.android.slinger.resolver.IntentResolver;
import pl.allegro.android.slinger.resolver.RedirectRule;

import static pl.allegro.android.slinger.IntentStarterTest.Utils.preparePackageManager;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static pl.allegro.android.slinger.resolver.RedirectRule.builder;

@RunWith(RobolectricGradleTestRunner.class) @Config(manifest = Config.NONE)
public class SlingerActivityTest {

  private static IntentEnricher intentEnricher;

  @Before public void setUp() throws PackageManager.NameNotFoundException {
    intentEnricher = spy(new DefaultIntentEnricher());
  }

  @Test(expected = RuntimeException.class) public void shouldFailWithoutSpecifyingUri() {
    // given
    Intent intent = new Intent(Intent.ACTION_VIEW);

    // when
    Robolectric.buildActivity(SlingerActivity.class).withIntent(intent).create().get();
  }

  @Test(expected = RuntimeException.class) public void shouldFailForNullIntent() {
    Robolectric.buildActivity(SlingerActivity.class).withIntent(null).create().get();
  }

  @Test public void shouldSetCustomIntentEnricher() throws PackageManager.NameNotFoundException {
    // given
    Uri uri = Uri.parse("http://example.com");
    Intent intent = new Intent(Intent.ACTION_VIEW, uri);

    String packageName = RuntimeEnvironment.application.getPackageName();
    preparePackageManager(
        new Intent(intent).setComponent(new ComponentName(packageName, Activity1.class.getName())),
        ImmutableList.<Class<? extends Activity>>of(Activity1.class));
    preparePackageManager(new Intent(intent).setComponent(
        new ComponentName(packageName, SlingerActivity.class.getName())),
        ImmutableList.<Class<? extends Activity>>of(SlingerActivity.class));
    new PackageManagerPreparator().getActivityInfo().metaData.putString(
        ManifestParser.INTENT_RESOLVER_NAME, TestIntentResolver.class.getName());

    // when
    SlingerActivity slingerActivity =
        Robolectric.buildActivity(SlingerActivity.class).withIntent(intent).create().get();

    //then
    verify(intentEnricher).enrichSlingedIntent(eq(slingerActivity), any(Uri.class), any(Intent.class));
  }

  static class Activity1 extends Activity {
  }

  public static class TestIntentResolver extends IntentResolver {

    public static final String PATTERN_FOR_EXAMPLE_HOST = "http(s)?://example.com.*";

    private List<RedirectRule> rules;

    public TestIntentResolver(Activity activity) {
      super(activity);
      rules = ImmutableList.of(getRedirectRuleForExampleHost(activity));
    }

    RedirectRule getRedirectRuleForExampleHost(Activity activity) {
      return builder().intent(new Intent(activity, Activity1.class).setAction(Intent.ACTION_VIEW))
          .pattern(PATTERN_FOR_EXAMPLE_HOST)
          .build();
    }

    @NonNull @Override public Iterable<RedirectRule> getRules() {
      return rules;
    }

    @Override public IntentEnricher getIntentEnricher() {
      return intentEnricher;
    }
  }
}
