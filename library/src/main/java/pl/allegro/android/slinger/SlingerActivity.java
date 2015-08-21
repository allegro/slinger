package pl.allegro.android.slinger;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import pl.allegro.android.slinger.enricher.DefaultIntentEnricher;
import pl.allegro.android.slinger.enricher.IntentEnricher;
import pl.allegro.android.slinger.resolver.IntentResolver;
import pl.allegro.android.slinger.resolver.RedirectRule;
import java.util.Collections;

public abstract class SlingerActivity extends Activity {

  private IntentEnricher intentEnricher = new DefaultIntentEnricher();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Uri uri = getOriginatingUriFromIntent(getIntent());

    if (uri == null) {
      finish();
      return;
    }

    excludeSlingerAndStartTargetActivity(uri);
    finish();
  }

  private Uri getOriginatingUriFromIntent(Intent intent) {
    return intent != null ? intent.getData() : null;
  }

  protected void excludeSlingerAndStartTargetActivity(Uri originatingUri) {
    new IntentStarter(getPackageManager(), enrichIntent(originatingUri),
        Collections.<Class<? extends Activity>>singletonList(this.getClass())).startActivity(this);
  }

  protected Intent enrichIntent(Uri originatingUri) {
    return intentEnricher.enrichSlingedIntent(this, originatingUri,
        resolveIntentToBeSlinged(originatingUri));
  }

  private Intent resolveIntentToBeSlinged(Uri originatingUri) {
    return getIntentResolver().resolveIntentToSling(originatingUri);
  }

  /**
   * @return {@link IntentResolver} which provides implementation with collection of {@link
   * RedirectRule}s
   * and default {@link Intent} when no {@link RedirectRule} is matched
   */
  protected abstract IntentResolver getIntentResolver();
}
