package pl.allegro.android.slinger;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.VisibleForTesting;
import java.util.List;

/**
 * Class bypassing App link mechanism introduced in Android Marshmallow.
 * If an application has link-handling setting enabled then {@link PackageManager} returns only one
 * result from {@link PackageManager#queryIntentActivities(Intent, int)}.
 * If we explicitly don't want to use this result then we query for {@link Intent}s only with
 * scheme defined.
 */
class AppLinkBypasser {
  private final PackageManager packageManager;

  public AppLinkBypasser(PackageManager packageManager) {
    this.packageManager = packageManager;
  }

  boolean isBypassApplicable(List<ResolveInfo> queryIntentActivities) {
    return hasMarshmallow() && onlyOneResult(queryIntentActivities);
  }

  private boolean onlyOneResult(List<ResolveInfo> queryIntentActivities) {
    return queryIntentActivities.size() == 1;
  }

  private boolean hasMarshmallow() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
  }

  List<ResolveInfo> resolveAdditionalActivitiesWithScheme(Intent intent) {
    return packageManager.queryIntentActivities(getIntentWithRawSchemeUri(intent), 0);
  }

  @VisibleForTesting Intent getIntentWithRawSchemeUri(Intent intent) {
    return new Intent(intent).setData(getRawSchemeUri(intent.getData()));
  }

  @VisibleForTesting Uri getRawSchemeUri(Uri uri) {
    return new Uri.Builder().scheme(uri.getScheme()).build();
  }
}
