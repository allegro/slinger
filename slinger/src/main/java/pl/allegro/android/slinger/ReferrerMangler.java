package pl.allegro.android.slinger;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;

/**
 * Utility class for retrieving and inserting referrers.
 */
public final class ReferrerMangler {

  public static final String EXTRA_REFERRER = "android.intent.extra.REFERRER";
  public static final String EXTRA_REFERRER_NAME = "android.intent.extra.REFERRER_NAME";

  private ReferrerMangler() {
  }

  /**
   * Gets referrer from {@link Activity} or {@link Intent}. Referrer in Android world is an {@link
   * Uri} that indicates from which place {@link Activity} was started.
   *
   * @param activity from which referrer would be extracted (if exists)
   * @return {@link Uri} with referrer if present or null
   */
  @Nullable public static Uri getReferrerUriFromActivity(@NonNull Activity activity) {
    if (SDK_INT >= LOLLIPOP_MR1) {
      return getLollipopReferrer(activity);
    }

    return getReferrerUriFromIntent(activity.getIntent());
  }

  public static Uri getReferrerUriFromIntent(Intent intent) {
    Uri referrerUri = intent.getParcelableExtra(EXTRA_REFERRER);
    if (referrerUri != null) {
      return referrerUri;
    }

    String referrer = intent.getStringExtra(EXTRA_REFERRER_NAME);
      return referrer != null ? Uri.parse(referrer) : null;
  }

  @TargetApi(LOLLIPOP_MR1) @Nullable
  private static Uri getLollipopReferrer(@NonNull Activity activity) {
    return activity.getReferrer();
  }

  /**
   * Adds referrer to intent
   *
   * @param intent in which referrerUri will be inserted
   * @param referrerUri to insert in {@link Intent}
   * @return {@link Intent} with referrer inside it
   */
  public static Intent addReferrerToIntent(Intent intent, Uri referrerUri) {
    if (referrerUri != null) {
      intent.putExtra(EXTRA_REFERRER, referrerUri);
    }

    return intent;
  }
}
