package pl.allegro.android.slinger;


import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import pl.allegro.android.slinger.resolver.IntentResolver;

/**
 * Parses {@link IntentResolver} references out of the AndroidManifest file.
 */
public final class ManifestParser {
  private static final String INTENT_RESOLVER_NAME = "IntentResolver";

  private final Activity activity;

  public ManifestParser(Activity activity) {
    this.activity = activity;
  }

  public IntentResolver parse() {
    try {
      ApplicationInfo appInfo = activity.getPackageManager()
          .getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
      if (appInfo.metaData != null) {
        for (String key : appInfo.metaData.keySet()) {
          if (INTENT_RESOLVER_NAME.equals(key)) {
            return parseResolver(appInfo.metaData.getString(key));
          }
        }
      }
    } catch (PackageManager.NameNotFoundException e) {
      throw new RuntimeException("Unable to find metadata to parse IntentResolver", e);
    }

    throw new RuntimeException("Unable to find metadata to parse IntentResolver");
  }

  private IntentResolver parseResolver(String className) {
    Class<?> clazz;
    try {
      clazz = Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Unable to find IntentResolver implementation", e);
    }

    Object module;
    try {
      Constructor<?> cons = clazz.getConstructor(Activity.class);
      module = cons.newInstance(activity);
    } catch (InstantiationException e) {
      throw new RuntimeException("Unable to instantiate IntentResolver implementation for " + clazz,
          e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Unable to instantiate IntentResolver implementation for " + clazz,
          e);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("No constructor for " + clazz + "that has Activity as parameter",
          e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Unable to instantiate IntentResolver implementation for " + clazz,
          e);
    }

    if (!(module instanceof IntentResolver)) {
      throw new RuntimeException("Expected instanceof IntentResolver, but found: " + module);
    }
    return (IntentResolver) module;
  }
}