package pl.allegro.android.slinger.example;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.List;

import pl.allegro.android.slinger.resolver.IntentResolver;
import pl.allegro.android.slinger.resolver.RedirectRule;

import static java.util.Arrays.asList;
import static pl.allegro.android.slinger.resolver.RedirectRule.builder;

public class ExampleIntentResolver extends IntentResolver {
  public static final String PATTERN_FOR_ABOUT_ACTIVITY = "http(s)?://example.com/abc\\.html\\?query=a.*";
  public static final String PATTERN_FOR_CONTACT_ACTIVITY = "http(s)?://example.com/abc\\.html\\?query=c.*";

  private List<RedirectRule> rules;

  public ExampleIntentResolver(Activity activity) {
    super(activity);
    rules = asList(getRedirectRuleForAbout(activity), getRedirectRuleForContact(activity));
  }

  RedirectRule getRedirectRuleForAbout(Activity activity) {
    return builder().intent(new Intent(activity, AboutActivity.class)).pattern(
        PATTERN_FOR_ABOUT_ACTIVITY).build();
  }

  RedirectRule getRedirectRuleForContact(Activity activity) {
    return builder().intent(new Intent(activity, ContactActivity.class)).pattern(
        PATTERN_FOR_CONTACT_ACTIVITY).build();
  }

  @NonNull @Override public Iterable<RedirectRule> getRules() {
    return rules;
  }
}
