package pl.allegro.android.slinger;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PackageManagerInstrumentator {
  private ApplicationInfo applicationInfo;

  void preparePackageManagerInActivity(Activity activity) throws PackageManager.NameNotFoundException {
    applicationInfo = new ApplicationInfo();
    applicationInfo.metaData = new Bundle();

    String packageName = "pl.allegro.slinger.test";
    when(activity.getPackageName()).thenReturn(packageName);
    PackageManager packageManager = mock(PackageManager.class);
    preparePackageManager(packageManager, packageName);
    when(activity.getPackageManager()).thenReturn(packageManager);
  }

  void preparePackageManager(PackageManager packageManager, String packageName) throws PackageManager.NameNotFoundException {
    when(packageManager.getApplicationInfo(eq(packageName), eq(PackageManager.GET_META_DATA)))
        .thenReturn(applicationInfo);

  }

  void addModuleToManifest(Class<?> moduleClass) {
    addToManifest(moduleClass.getName());
  }

  void addToManifest(String value) {
    applicationInfo.metaData.putString(ManifestParser.INTENT_RESOLVER_NAME, value);
  }

  public ApplicationInfo getApplicationInfo() {
    return applicationInfo;
  }
}
