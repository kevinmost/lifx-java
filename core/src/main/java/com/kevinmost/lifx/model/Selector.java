package com.kevinmost.lifx.model;

import org.jetbrains.annotations.NotNull;

public abstract class Selector {

  @NotNull public static Selector unsafe(@NotNull final String unsafeString) {
    return new Selector() {
      @NotNull @Override public String toString() {
        return unsafeString;
      }
    };
  }

  @NotNull public static Selector forLight(@NotNull String label) {
    return new Impl("label", label);
  }

  @NotNull public static Selector forLight(@NotNull Light light) {
    return new Impl("id", light.id());
  }

  @NotNull public static Selector forGroup(@NotNull String label) {
    return new Impl("group", label);
  }

  @NotNull public static Selector forGroup(@NotNull LifxGroup group) {
    return new Impl("group_id", group.id());
  }

  @NotNull public static Selector forLocation(@NotNull String label) {
    return new Impl("location", label);
  }

  @NotNull public static Selector forLocation(@NotNull LifxLocation location) {
    return new Impl("location_id", location.id());
  }

  @NotNull public static final Selector ALL = new Selector() {
    @NotNull @Override public String toString() {
      return "all";
    }
  };

  @NotNull public abstract String toString();

  private static final class Impl extends Selector {

    @NotNull private final String prefix;
    @NotNull private final String suffix;

    private Impl(@NotNull String prefix, @NotNull String suffix) {
      this.prefix = prefix;
      this.suffix = suffix;
    }

    @NotNull @Override public String toString() {
      return prefix + ":" + suffix;
    }
  }
}
