package pl.allegro.android.slinger.enricher;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import pl.allegro.android.slinger.resolver.IntentResolver;

public interface IntentEnricher {

  /**
   * Enriches intent to be sligned with custom data.
   *
   * @param activity {@link Activity}that receives originating intent
   * @param originatingUri {@link Uri} retrieved from {@link Intent#getData()}
   * @param intentToBeSlinged {@link Intent} that was resolved by {@link IntentResolver}
   * @return Intent to be slinged
   */
  @NonNull Intent enrichSlingedIntent(@NonNull Activity activity, @Nullable Uri originatingUri,
      @NonNull Intent intentToBeSlinged);
}
