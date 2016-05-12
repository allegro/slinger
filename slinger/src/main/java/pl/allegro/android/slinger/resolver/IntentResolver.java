package pl.allegro.android.slinger.resolver;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import static android.content.Intent.ACTION_VIEW;
import static pl.allegro.android.slinger.ReferrerMangler.addReferrerToIntent;
import static pl.allegro.android.slinger.ReferrerMangler.getReferrerUriFromActivity;

/**
 * Class that resolves target {@link Intent} by matching {@link Uri} that started {@link Activity}
 * with pattern provided by {@link RedirectRule}
 */
public abstract class IntentResolver {

  @SuppressWarnings("unused")
  public IntentResolver(Activity activity) {
  }

  /**
   * Resolves {@link Intent} that will be slinged
   *
   * @param originatingUri {@link Uri} retrieved from {@link Intent#getData()}
   * @return {@link Intent} from matching {@link RedirectRule}
   */
  @NonNull
  public Intent resolveIntentToSling(@NonNull Uri originatingUri) {
    Intent matchingIntent = getMatchingIntentForRedirectRules(originatingUri);
    return matchingIntent != null ? matchingIntent : getDefaultRedirectIntent(originatingUri);
  }

  /**
   * Checks if {@link Uri} can be handled by provided redirect rules.
   * @param originatingUri {@link Uri} to check
   */
  public boolean canUriBeHandledByRedirectRules(@NonNull Uri originatingUri) {
    return getMatchingIntentForRedirectRules(originatingUri) != null;
  }

  private Intent getMatchingIntentForRedirectRules(@NonNull Uri originatingUri) {
    for (RedirectRule rule : getRules()) {
      if (isUriMatchingPattern(originatingUri, rule)) {
        return rule.getIntent();
      }
    }

    return null;
  }

  private boolean isUriMatchingPattern(Uri originatingUri, RedirectRule redirectable) {
    return redirectable.getPattern().matcher(originatingUri.toString()).matches();
  }

  /**
   * @return {@link Iterable} with {@link RedirectRule}s
   */
  @NonNull
  public abstract Iterable<RedirectRule> getRules();

  /**
   * @param originatingUri that started {@link Activity}
   * @return default {@link Intent} when there is no {@link RedirectRule} matching {@link Uri} that
   * started {@link Activity}
   */
  @NonNull
  protected Intent getDefaultRedirectIntent(Uri originatingUri) {
    return new Intent(ACTION_VIEW, originatingUri);
  }

  @NonNull
  public Intent enrichIntent(Activity parentActivity, Intent resolvedIntent, Uri originatingUri) {
    // we need to inform our target Activity about originating Uri
    resolvedIntent.setData(originatingUri);

    return addReferrerToIntent(resolvedIntent, getReferrerUriFromActivity(parentActivity));
  }
}
