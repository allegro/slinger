package pl.allegro.android.slinger.enricher;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import static pl.allegro.android.slinger.enricher.ReferrerMangler.addReferrerToIntent;
import static pl.allegro.android.slinger.enricher.ReferrerMangler.getReferrerUriFromActivity;

/**
 * Default implementation enriches slinged Intent with:
 * <ul>
 * <li>{@link Uri} from originating {@link Activity}</li>
 * <li>Referrer retrieved from {@link Activity} or calling {@link Intent}</li>
 * </ul>
 */
public class DefaultIntentEnricher implements IntentEnricher {

  @NonNull @Override
  public Intent enrichSlingedIntent(@NonNull Activity activity, Uri originatingUri,
      @NonNull Intent intentToBeSlinged) {

    // we need to inform our target Activity about originating Uri
    intentToBeSlinged.setData(originatingUri);

    return addReferrerToIntent(intentToBeSlinged, getReferrerUriFromActivity(activity));
  }
}
