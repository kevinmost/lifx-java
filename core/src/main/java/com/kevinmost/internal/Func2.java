package com.kevinmost.internal;

import org.jetbrains.annotations.NotNull;

public interface Func2<T1, T2, R> {
  @NotNull R call(@NotNull T1 p1, @NotNull T2 p2);
}
