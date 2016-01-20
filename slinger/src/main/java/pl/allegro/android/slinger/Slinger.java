package pl.allegro.android.slinger;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import java.util.Collections;
import pl.allegro.android.slinger.enricher.DefaultIntentEnricher;
import pl.allegro.android.slinger.enricher.IntentEnricher;
import pl.allegro.android.slinger.resolver.IntentResolver;
import pl.allegro.android.slinger.resolver.RedirectRule;

public class Slinger {

  private static IntentEnricher intentEnricher = new DefaultIntentEnricher();

  /**
   * Starts new {@link Intent} resolved by {@link IntentResolver}
   * Starts passed intent and excludes <code>parentActivity</code>
   *
   * @param parentActivity that is used to be excluded from launch
   * @param intent contains uri that is used to find a new {@link Intent}
   */
  public static void startActivity(Activity parentActivity, Intent intent) {
    Uri uri = getOriginatingUriFromIntent(intent);

    if (uri == null) {
      throw new RuntimeException(
          "You cannot run this Activity without specifying Uri inside Intent!");
    }

    excludeSlingerAndStartTargetActivity(parentActivity,
        intentEnricher.enrichSlingedIntent(parentActivity, uri,
            resolveIntentToBeSlinged(parentActivity, uri)));
  }

  private static Uri getOriginatingUriFromIntent(Intent intent) {
    return intent != null ? intent.getData() : null;
  }

  private static void excludeSlingerAndStartTargetActivity(Activity parentActivity, Intent intent) {
    new IntentStarter(parentActivity.getPackageManager(), intent,
        Collections.<Class<? extends Activity>>singletonList(SlingerActivity.class)).startActivity(
        parentActivity);
  }

  private static Intent resolveIntentToBeSlinged(Activity parentActivity, Uri originatingUri) {
    return getIntentResolver(parentActivity).resolveIntentToSling(originatingUri);
  }

  /**
   * @return {@link IntentResolver} which provides implementation with collection of {@link
   * RedirectRule}s
   * and default {@link Intent} when no {@link RedirectRule} is matched
   */
  private static IntentResolver getIntentResolver(Activity parentActivity) {
    return new ManifestParser(parentActivity).parse();
  }
}