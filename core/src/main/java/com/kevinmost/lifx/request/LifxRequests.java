package com.kevinmost.lifx.request;

import com.kevinmost.lifx.model.LifxEntity;
import com.kevinmost.lifx.model.LifxColor;
import com.kevinmost.lifx.model.Selector;
import org.jetbrains.annotations.NotNull;

public final class LifxRequests {
  private LifxRequests() { throw new UnsupportedOperationException("No instances"); }

  @NotNull public static ListLightsRequest listLights() {
    return listLights(Selector.ALL);
  }

  @NotNull public static ListLightsRequest listLights(@NotNull LifxEntity lifxEntity) {
    return listLights(lifxEntity.selector());
  }

  @NotNull public static ListLightsRequest listLights(@NotNull Selector selector) {
    return new ListLightsRequest(selector);
  }

  @NotNull public static VerifyColorRequest verifyColor(@NotNull LifxColor color) {
    return verifyColor(color.toString());
  }

  @NotNull public static VerifyColorRequest verifyColor(@NotNull String colorString) {
    return new VerifyColorRequest(colorString);
  }

  @NotNull public static SetLightsRequest setLights() {
    return new SetLightsRequest();
  }
}
