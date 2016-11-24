package com.kevinmost.lifx.model;

import org.jetbrains.annotations.NotNull;

public interface LifxEntity {
  @NotNull String id();
  @NotNull Selector selector();
}
