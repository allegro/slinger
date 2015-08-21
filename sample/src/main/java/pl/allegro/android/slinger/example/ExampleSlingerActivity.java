package pl.allegro.android.slinger.example;

import android.content.Intent;
import pl.allegro.android.slinger.SlingerActivity;
import pl.allegro.android.slinger.resolver.IntentResolver;
import pl.allegro.android.slinger.resolver.RedirectRule;

import static java.util.Arrays.asList;
import static pl.allegro.android.slinger.resolver.RedirectRule.builder;

public class ExampleSlingerActivity extends SlingerActivity {
  public static final String PATTERN_FOR_ABOUT_ACTIVITY = "http(s)?://example.com/abc\\.html\\?query=a.*";
  public static final String PATTERN_FOR_CONTACT_ACTIVITY = "http(s)?://example.com/abc\\.html\\?query=c.*";

  RedirectRule getRedirectRuleForAbout() {
    return builder().intent(new Intent(this, AboutActivity.class)).pattern(
        PATTERN_FOR_ABOUT_ACTIVITY).build();
  }

  RedirectRule getRedirectRuleForContact() {
    return builder().intent(new Intent(this, ContactActivity.class)).pattern(
        PATTERN_FOR_CONTACT_ACTIVITY).build();
  }

  @Override protected IntentResolver getIntentResolver() {
    return new IntentResolver(asList(getRedirectRuleForAbout(), getRedirectRuleForContact()));
  }
}
