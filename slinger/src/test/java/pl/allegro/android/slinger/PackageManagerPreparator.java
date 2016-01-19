package pl.allegro.android.slinger;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.res.builder.RobolectricPackageManager;

import static android.content.pm.PackageManager.GET_META_DATA;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class PackageManagerPreparator {
  private final ActivityInfo activityInfo = new ActivityInfo();

  PackageManagerPreparator() throws PackageManager.NameNotFoundException {
    RobolectricPackageManager packageManager =
        spy((RobolectricPackageManager) RuntimeEnvironment.application.getPackageManager());

    RuntimeEnvironment.setRobolectricPackageManager(packageManager);
    doReturn(activityInfo).when(packageManager).getActivityInfo(any(ComponentName.class), eq(GET_META_DATA));
    activityInfo.metaData = new Bundle();
  }

  void addModuleToManifest(Class<?> moduleClass) {
    addToManifest(moduleClass.getName());
  }

  void addToManifest(String value) {
    activityInfo.metaData.putString(ManifestParser.INTENT_RESOLVER_NAME, value);
  }

  public ActivityInfo getActivityInfo() {
    return activityInfo;
  }
}
