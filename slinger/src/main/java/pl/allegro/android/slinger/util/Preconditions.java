package pl.allegro.android.slinger.util;

import android.support.annotation.Nullable;

public final class Preconditions {
  private Preconditions() {
  }

  public static <T> T checkNotNull(T reference, @Nullable Object errorMessage) {
    if (reference == null) {
      throw new IllegalArgumentException(String.valueOf(errorMessage));
    }
    return reference;
  }
}
