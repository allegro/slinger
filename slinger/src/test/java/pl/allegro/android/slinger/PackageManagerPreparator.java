package pl.allegro.android.slinger;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.res.builder.RobolectricPackageManager;

public class PackageManagerPreparator {
  private ApplicationInfo applicationInfo;

  PackageManagerPreparator(String packageName) throws PackageManager.NameNotFoundException {
    RobolectricPackageManager packageManager =
        (RobolectricPackageManager) RuntimeEnvironment.application.getPackageManager();

    PackageInfo packageInfo = new PackageInfo();
    packageInfo.packageName = packageName;
    applicationInfo = new ApplicationInfo();
    applicationInfo.metaData = new Bundle();
    packageInfo.applicationInfo = applicationInfo;
    packageManager.addPackage(packageInfo);
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
