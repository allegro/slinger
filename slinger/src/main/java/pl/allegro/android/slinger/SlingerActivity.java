package pl.allegro.android.slinger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import pl.allegro.android.slinger.enricher.IntentEnricher;

public class SlingerActivity extends Activity {

  private static IntentEnricher intentEnricher;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = getIntent();
    if (intentEnricher != null) {
      Slinger.startActivity(this, intent);
    } else {
      Slinger.startActivity(this, intent, intentEnricher);
    }
    finish();
  }

  public static void setIntentEnricher(IntentEnricher intentEnricher) {
    SlingerActivity.intentEnricher = intentEnricher;
  }
}
