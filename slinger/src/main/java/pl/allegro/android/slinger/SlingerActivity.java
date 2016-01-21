package pl.allegro.android.slinger;

import android.app.Activity;
import android.os.Bundle;

public class SlingerActivity extends Activity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Slinger.startActivity(this, getIntent());
    finish();
  }
}
