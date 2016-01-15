package pl.allegro.android.slinger;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;

import pl.allegro.android.slinger.resolver.IntentResolver;
import pl.allegro.android.slinger.resolver.RedirectRule;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ManifestParserTest {
  private PackageManagerInstrumentator instrumentator = new PackageManagerInstrumentator();
  private ManifestParser parser;
  private Activity activity = spy(Robolectric.setupActivity(Activity.class));

  @Before
  public void setUp() throws PackageManager.NameNotFoundException {
    instrumentator.preparePackageManagerInActivity(activity);
    parser = new ManifestParser(activity);
  }

  @Test(expected = RuntimeException.class)
  public void shouldFailIfThereAreNoModules() {
    assertThat(parser.parse());
  }

  @Test
  public void shouldParseSingleModule() {
    instrumentator.addModuleToManifest(TestModule1.class);

    IntentResolver modules = parser.parse();
    assertThat(modules).isInstanceOf(TestModule1.class);
  }

  @Test(expected = RuntimeException.class)
  public void shouldFailIfAddedWithWrongKey() {
    instrumentator.getApplicationInfo().metaData.putString(ManifestParser.INTENT_RESOLVER_NAME + "test", TestModule1.class.getName());
    parser.parse();
  }

  @Test(expected = RuntimeException.class)
  public void shouldFailIfFakeClassNameWasAdded() {
    instrumentator.addToManifest("fakeClassName");

    parser.parse();
  }

  @Test(expected = RuntimeException.class)
  public void shouldFailIfInvalidClassNameWasAdded() {
    instrumentator.addModuleToManifest(InvalidClass.class);

    parser.parse();
  }

  @Test(expected = RuntimeException.class)
  public void shouldFailIfPackageNotFound() {
    when(activity.getPackageName()).thenReturn("fakePackageName");

    parser.parse();
  }

  public static class InvalidClass {
  }

  public static class TestModule1 extends IntentResolver {

    public TestModule1(Activity activity) {
      super(activity);
    }

    @NonNull
    @Override
    public Iterable<RedirectRule> getRules() {
      return new ArrayList<>();
    }
  }

  public static class TestModule2 extends IntentResolver {

    public TestModule2(Activity activity) {
      super(activity);
    }

    @NonNull
    @Override
    public Iterable<RedirectRule> getRules() {
      return new ArrayList<>();
    }
  }

}