package pl.allegro.android.slinger;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.res.builder.RobolectricPackageManager;

import java.util.ArrayList;
import java.util.List;

import pl.allegro.android.slinger.util.IntentMatchers.IsIntentWithAction;
import pl.allegro.android.slinger.util.IntentMatchers.IsIntentWithPackageName;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.allegro.android.slinger.IntentStarterTest.Utils.preparePackageManager;

@RunWith(RobolectricGradleTestRunner.class) public class IntentStarterTest {

  @Test public void activityIsIgnoredGivingEmptyListOfTargetIntents() {
    // given
    Intent intent = new Intent();
    PackageManager packageManager =
        preparePackageManager(intent, ImmutableList.<Class<? extends Activity>>of(Activity1.class));
    IntentStarter objectUnderTest =
        new IntentStarter(packageManager, intent, Activity1.class, "", null);

    // when
    objectUnderTest.resolveActivities();

    // then
    assertThat(objectUnderTest.getTargetIntents()).isEmpty();
  }

  @Test public void manyActivitiesAreIgnoredGivingEmptyListOfTargetIntents() {
    // given
    Intent intent = new Intent();
    ImmutableList<Class<? extends Activity>> activitiesToResolveAndIgnore =
        ImmutableList.of(Activity1.class, Activity2.class);
    PackageManager packageManager = preparePackageManager(intent, activitiesToResolveAndIgnore);
    IntentStarter objectUnderTest =
        new IntentStarter(packageManager, intent, activitiesToResolveAndIgnore, "", null);

    // when
    objectUnderTest.resolveActivities();

    // then
    assertThat(objectUnderTest.getTargetIntents()).isEmpty();
  }

  @Test public void shouldResolveOneActivity() {
    // given
    Intent intent = new Intent();
    PackageManager packageManager =
        preparePackageManager(intent, ImmutableList.<Class<? extends Activity>>of(Activity1.class));
    IntentStarter objectUnderTest = new IntentStarter(packageManager, intent);

    // when
    objectUnderTest.resolveActivities();

    // then
    List<Intent> targetIntents = objectUnderTest.getTargetIntents();
    assertThat(targetIntents).hasSize(1);
    assertThat(targetIntents.get(0).getPackage()).isEqualTo(Activity1.class.getPackage().getName());
  }

  @Test public void whenThereIsDefaultHandlerThenActivityWillBeStartedWithOriginalIntent() {
    // given
    Intent intent = new Intent();

    PackageManager packageManager = mock(PackageManager.class);
    when(packageManager.resolveActivity(eq(intent), anyInt())).thenReturn(
        Utils.createResolveInfo(Activity3.class, true));

    IntentStarter objectUnderTest =
        new IntentStarter(packageManager, intent, Activity1.class, "", null);
    Activity parentActivity = getParentActivitySpy();

    // when
    objectUnderTest.startActivity(parentActivity);

    // then
    verify(parentActivity).startActivity(intent);
  }

  @NonNull private Activity getParentActivitySpy() {
    return spy(Activity1.class);
  }

  @Test
  public void whenIgnoredActivityIsDefaultHandlerAndThereIsOnlyOneMoreActivityAbleToHandleTheIntentThenTheOtherOneIsStarted() {
    // given
    Intent intent = new Intent();

    PackageManager packageManager = mock(PackageManager.class);
    when(packageManager.queryIntentActivities(eq(intent), anyInt())).thenReturn(
        Utils.makeResolveInfoList(Activity1.class, Activity2.class));
    when(packageManager.resolveActivity(eq(intent), anyInt())).thenReturn(
        Utils.createResolveInfo(Activity1.class, true));

    IntentStarter objectUnderTest =
        new IntentStarter(packageManager, intent, Activity1.class, "", null);
    Activity parentActivity = getParentActivitySpy();

    // when
    objectUnderTest.startActivity(parentActivity);

    // then
    verify(parentActivity).startActivity(
        argThat(new IsIntentWithPackageName("pl.allegro.android.slinger")));
  }

  @Test
  public void whenIgnoredActivityIsDefaultHandlerAndThereAreMoreActivitiesAbleToHandleTheIntentThenChooserIsPresented() {
    // given
    Intent intent = new Intent();

    PackageManager packageManager = mock(PackageManager.class);
    when(packageManager.queryIntentActivities(eq(intent), anyInt())).thenReturn(
        Utils.makeResolveInfoList(Activity1.class, Activity2.class, Activity3.class));
    when(packageManager.resolveActivity(eq(intent), anyInt())).thenReturn(
        Utils.createResolveInfo(Activity1.class, true));

    IntentStarter objectUnderTest =
        new IntentStarter(packageManager, intent, Activity1.class, "", null);
    Activity parentActivity = getParentActivitySpy();

    // when
    objectUnderTest.startActivity(parentActivity);

    // then
    verify(parentActivity).startActivity(
        argThat(new IsIntentWithAction("android.intent.action.CHOOSER")));
  }

  @Test public void whenThereIsOnlyOneAdditionalActivityAbleToHandleIntentThenItWillBeStarted() {
    // given
    Intent intent = new Intent();
    PackageManager packageManager =
        preparePackageManager(intent, ImmutableList.of(Activity1.class, Activity2.class));
    IntentStarter objectUnderTest =
        new IntentStarter(packageManager, intent, Activity1.class, "", null);
    Activity parentActivity = getParentActivitySpy();

    // when
    objectUnderTest.startActivity(parentActivity);

    // then
    verify(parentActivity).startActivity(
        argThat(new IsIntentWithPackageName("pl.allegro.android.slinger")));
  }

  @Test public void whenThereAreMultipleActivitiesAbleToHandleIntentThenChooserWillBePresented() {
    // given
    Intent intent = new Intent();
    PackageManager packageManager = preparePackageManager(intent,
        ImmutableList.of(Activity1.class, Activity2.class, Activity3.class));
    IntentStarter objectUnderTest =
        new IntentStarter(packageManager, intent, Activity1.class, "", null);
    Activity parentActivity = getParentActivitySpy();

    // when
    objectUnderTest.startActivity(parentActivity);

    // then
    verify(parentActivity).startActivity(
        argThat(new IsIntentWithAction("android.intent.action.CHOOSER")));
  }

  static class Activity1 extends Activity {

  }

  static class Activity2 extends Activity {

  }

  static class Activity3 extends Activity {

  }

  static class Utils {

    private static ActivityInfo createActvitiyInfo(Class<? extends Activity> clazz) {
      ActivityInfo activityInfo = new ActivityInfo();
      activityInfo.name = clazz.getCanonicalName();
      activityInfo.packageName = clazz.getPackage().getName();
      return activityInfo;
    }

    private static ResolveInfo createResolveInfo(Class<? extends Activity> clazz) {
      return createResolveInfo(clazz, false);
    }

    static ResolveInfo createResolveInfo(Class<? extends Activity> clazz, boolean isDefault) {
      ResolveInfo resolveInfo = new ResolveInfo();
      resolveInfo.activityInfo = createActvitiyInfo(clazz);
      resolveInfo.isDefault = isDefault;
      return resolveInfo;
    }

    static PackageManager preparePackageManager(Intent intent,
        List<Class<? extends Activity>> classes) {
      RobolectricPackageManager packageManager =
          (RobolectricPackageManager) RuntimeEnvironment.application.getPackageManager();
      for (Class<? extends Activity> clazz : classes) {
        packageManager.addResolveInfoForIntent(intent, createResolveInfo(clazz));
      }
      return (PackageManager) packageManager;
    }

    @SafeVarargs
    static List<ResolveInfo> makeResolveInfoList(Class<? extends Activity>... classes) {
      List<ResolveInfo> list = new ArrayList<>(classes.length);
      for (Class<? extends Activity> clazz : classes) {
        list.add(createResolveInfo(clazz));
      }
      return list;
    }
  }
}