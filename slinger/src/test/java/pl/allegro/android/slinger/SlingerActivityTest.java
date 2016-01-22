package pl.allegro.android.slinger;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import pl.allegro.android.slinger.resolver.IntentResolver;
import pl.allegro.android.slinger.resolver.RedirectRule;

import static pl.allegro.android.slinger.resolver.RedirectRule.builder;

@RunWith(RobolectricGradleTestRunner.class) @Config(manifest = Config.NONE)
public class SlingerActivityTest {

  @Test(expected = RuntimeException.class) public void shouldFailWithoutSpecifyingUri() {
    // given
    Intent intent = new Intent(Intent.ACTION_VIEW);

    // when
    Robolectric.buildActivity(SlingerActivity.class).withIntent(intent).create().get();
  }

  @Test(expected = RuntimeException.class) public void shouldFailForNullIntent() {
    Robolectric.buildActivity(SlingerActivity.class).withIntent(null).create().get();
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
  }
}
