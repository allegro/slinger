package pl.allegro.android.slinger;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Intent.EXTRA_INITIAL_INTENTS;
import static android.content.Intent.createChooser;
import static android.content.pm.PackageManager.MATCH_DEFAULT_ONLY;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

/**
 * Starts Intent but without {@link Activity} that should be ignored.
 */
public class IntentStarter {
  public static final String RESOLVER_ACTIVITY = "com.android.internal.app.ResolverActivity";
  private final List<String> activitiesToIgnore;
  private final PackageManager packageManager;
  private final Intent intent;
  private final List<Intent> targetIntents = new ArrayList<>();
  private final String resolverTitle;
  private final AppLinkBypasser appLinkBypasser;
  private boolean wasResolved;

  public IntentStarter(@NonNull PackageManager packageManager, @NonNull Intent intent) {
    this(packageManager, intent, Collections.<Class<? extends Activity>>emptyList(), "", null);
  }

  public IntentStarter(@NonNull PackageManager packageManager, @NonNull Intent intent,
      @Nullable Class<? extends Activity> activityToIgnore, @Nullable String title,
      @Nullable AppLinkBypasser appLinkBypasser) {
    this.packageManager = packageManager;
    this.intent = intent;

    this.activitiesToIgnore = activityToIgnore != null ? getActivitiesCanonicalNames(
        Collections.<Class<? extends Activity>>singletonList(activityToIgnore))
        : Collections.<String>emptyList();
    this.resolverTitle = title;
    this.appLinkBypasser = createOrGetAppLinkBypasser(packageManager, appLinkBypasser);
  }

  public IntentStarter(@NonNull PackageManager packageManager, @NonNull Intent intent,
      @Nullable List<Class<? extends Activity>> activitiesToIgnore, @Nullable String title,
      @Nullable AppLinkBypasser appLinkBypasser) {
    this.packageManager = packageManager;
    this.intent = intent;
    this.activitiesToIgnore = getIgnoredActivitiesList(activitiesToIgnore);
    this.resolverTitle = title;
    this.appLinkBypasser = createOrGetAppLinkBypasser(packageManager, appLinkBypasser);
  }

  @NonNull
  private AppLinkBypasser createOrGetAppLinkBypasser(@NonNull PackageManager packageManager,
      AppLinkBypasser appLinkBypasser) {
    return appLinkBypasser != null ? appLinkBypasser : new AppLinkBypasser(packageManager);
  }

  public IntentStarter(@NonNull PackageManager packageManager, @NonNull Intent intent,
      @Nullable List<Class<? extends Activity>> activitiesToIgnore) {
    this(packageManager, intent, activitiesToIgnore, "", null);
  }

  private List<String> getIgnoredActivitiesList(
      @Nullable List<Class<? extends Activity>> activitiesToIgnore) {
    if (activitiesToIgnore != null) {
      return getActivitiesCanonicalNames(activitiesToIgnore);
    }
    return Collections.emptyList();
  }

  private List<String> getActivitiesCanonicalNames(
      List<Class<? extends Activity>> activitiesToIgnore) {
    List<String> activityNames = new ArrayList<>();
    for (Class classObject : activitiesToIgnore) {
      activityNames.add(classObject.getCanonicalName());
    }
    return activityNames;
  }

  void resolveActivities() {
    if (wasResolved) {
      targetIntents.clear();
    }
    wasResolved = true;
    List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);

    if (appLinkBypasser.isBypassApplicable(queryIntentActivities)) {
      queryIntentActivities.addAll(appLinkBypasser.resolveAdditionalActivitiesWithScheme(intent));
    }

    for (ResolveInfo resolveInfo : queryIntentActivities) {
      PackageItemInfo resolvedActivityInfo = resolveInfo.activityInfo;
      if (!isActivityToBeIgnored(resolvedActivityInfo)) {

        if (queryIntentActivities.size() == 2 || resolveInfo.isDefault) {
          clearExistingAndAddDefaultIntent(resolvedActivityInfo);
          break;
        }

        addIntentWithExplicitPackageName(resolvedActivityInfo);
      }
    }
  }

  private void addIntentWithExplicitPackageName(PackageItemInfo resolvedActivityInfo) {
    targetIntents.add((new Intent(intent)).setPackage(resolvedActivityInfo.packageName));
  }

  private void clearExistingAndAddDefaultIntent(PackageItemInfo resolvedActivityInfo) {
    targetIntents.clear();
    targetIntents.add(0, (new Intent(intent)).setPackage(resolvedActivityInfo.packageName));
  }

  private boolean isActivityToBeIgnored(PackageItemInfo resolvedActivityInfo) {
    for (String activityToIgnore : activitiesToIgnore) {
      if (activityToIgnore.equals(resolvedActivityInfo.name)) {
        return true;
      }
    }
    return false;
  }

  List<Intent> getTargetIntents() {
    return Collections.unmodifiableList(targetIntents);
  }

  boolean hasDefaultHandler() {
    ResolveInfo resolvedActivity = packageManager.resolveActivity(intent, MATCH_DEFAULT_ONLY);
    if (resolvedActivity == null) {
      ComponentName component = intent.getComponent();
      if (component != null) {
        throw new ActivityNotFoundException("Unable to find explicit activity class "
            + component.toShortString()
            + "; have you declared this activity in your AndroidManifest.xml?");
      }
      throw new ActivityNotFoundException("No Activity found to handle " + intent);
    }

    ActivityInfo resolvedActivityInfo = resolvedActivity.activityInfo;
    return !RESOLVER_ACTIVITY.equals(resolvedActivityInfo.name) && !isActivityToBeIgnored(
        resolvedActivityInfo);
  }

  public void startActivity(Activity parentActivity) {
    if (parentActivity == null) {
      return;
    }

    if (!wasResolved) {
      resolveActivities();
    }
    if (hasDefaultHandler()) {
      runDefaultActivity(parentActivity);
    } else if (targetIntents.size() == 1) {
      runFirstAndOnlyOneActivity(parentActivity, targetIntents.get(0));
    } else if (!targetIntents.isEmpty()) {
      showChooser(parentActivity);
    } else {
      makeText(parentActivity, R.string.no_activities_to_handle_this_link, LENGTH_LONG).show();
    }
  }

  private void runDefaultActivity(Context context) {
    context.startActivity(intent);
  }

  private void runFirstAndOnlyOneActivity(Context context, Intent intent) {
    context.startActivity(intent);
  }

  private void showChooser(Activity activity) {
    List<Intent> intentsList = getIntentList();

    Intent chooserIntent =
        createChooser(targetIntents.get(0), resolverTitle).putExtra(EXTRA_INITIAL_INTENTS,
            intentsList.toArray(new Parcelable[intentsList.size()]));
    activity.startActivity(chooserIntent);
  }

  private List<Intent> getIntentList() {
    return targetIntents.subList(1, targetIntents.size());
  }
}