package pl.allegro.android.slinger.resolver;

import android.content.Intent;
import android.support.annotation.NonNull;
import java.util.regex.Pattern;

import static pl.allegro.android.slinger.util.Preconditions.checkNotNull;

/**
 * Redirect Rule which holds regular expression and {@link Intent} corresponding to it.
 */
public class RedirectRule {

  private final String regexpPattern;
  private final Intent intent;

  public RedirectRule(@NonNull Intent intent, @NonNull String regexpPattern) {
    this.intent = checkNotNull(intent, "intent == null");
    this.regexpPattern = checkNotNull(regexpPattern, "pattern == null");
  }

  public Intent getIntent() {
    return intent;
  }

  public Pattern getPattern() {
    return Pattern.compile(regexpPattern);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Intent intent;
    private String regexp;

    public Builder intent(@NonNull Intent intent) {
      this.intent = checkNotNull(intent, "intent == null");
      return this;
    }

    public Builder pattern(@NonNull String pattern) {
      this.regexp = checkNotNull(pattern, "pattern == null");
      return this;
    }

    public RedirectRule build() {
      return new RedirectRule(intent, regexp);
    }
  }
}
