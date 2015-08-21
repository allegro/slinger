package pl.allegro.android.slinger.util;

import android.content.Intent;
import android.support.annotation.NonNull;
import org.mockito.ArgumentMatcher;

public class IntentMatchers {

  public static class IsIntentWithAction extends ArgumentMatcher<Intent> {

    private String action;

    public IsIntentWithAction(@NonNull String action) {
      this.action = action;
    }

    @Override public boolean matches(@NonNull Object argument) {
      return action.equals(((Intent) argument).getAction());
    }
  }

  public static class IsIntentWithPackageName extends ArgumentMatcher<Intent> {

    private String packageName;

    public IsIntentWithPackageName(@NonNull String packageName) {
      this.packageName = packageName;
    }

    @Override public boolean matches(@NonNull Object argument) {
      return packageName.equals(((Intent) argument).getPackage());
    }
  }
}
