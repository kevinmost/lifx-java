package com.kevinmost.lifx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum EnvVar {
  LIFX_ACCESS_TOKEN,
  ;

  @NotNull private final String key;

  EnvVar() {
    this.key = name();
  }

  EnvVar(@NotNull String key) {
    this.key = key;
  }

  @Nullable public String value() {
    return System.getenv(key);
  }
}
