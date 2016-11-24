package com.kevinmost.internal;

import org.jetbrains.annotations.NotNull;

public interface Func1<T, R> {
  @NotNull R call(@NotNull T in);
}
