package pl.allegro.android.slinger.resolver;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import static android.content.Intent.ACTION_VIEW;

/**
 * Class that resolves target {@link Intent} by matching {@link Uri} that started {@link Activity}
 * with pattern provided by {@link RedirectRule}
 */
@Keep
public abstract class IntentResolver {

  public IntentResolver(Activity activity){
  }

  /**
   * Resolves {@link Intent} that will be slinged
   *
   * @param originatingUri {@link Uri} retrieved from {@link Intent#getData()}
   * @return {@link Intent} from matchibng {@link RedirectRule}
   */
  @NonNull public Intent resolveIntentToSling(@NonNull Uri originatingUri) {
    Intent intentToSling = getDefaultRedirectIntent(originatingUri);

    for (RedirectRule rule : getRules()) {
      if (isUriMatchingPattern(originatingUri, rule)) {
        intentToSling = rule.getIntent();
        break;
      }
    }

    return intentToSling;
  }

  private boolean isUriMatchingPattern(Uri originatingUri, RedirectRule redirectable) {
    return redirectable.getPattern().matcher(originatingUri.toString()).matches();
  }

  /**
   * @return {@link Iterable} with {@link RedirectRule}s
   */
  @NonNull public abstract Iterable<RedirectRule> getRules();

  /**
   * @param originatingUri that started {@link Activity}
   * @return default {@link Intent} when there is no {@link RedirectRule} matching {@link Uri} that
   * started {@link Activity}
   */
  @NonNull protected Intent getDefaultRedirectIntent(Uri originatingUri) {
    return new Intent(ACTION_VIEW, originatingUri);
  }
}
