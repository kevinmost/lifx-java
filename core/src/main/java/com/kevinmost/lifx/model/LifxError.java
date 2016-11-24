package com.kevinmost.lifx.model;

import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@AutoValue
public abstract class LifxError {

  @NotNull public abstract String field();
  @NotNull public abstract List<String> message();

  LifxError() {} // AutoValue instances only
}