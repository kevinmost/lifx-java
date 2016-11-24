package com.kevinmost.lifx.request.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import com.kevinmost.internal.JSONObjectBuilder;
import com.kevinmost.internal.JsonUtil;
import com.kevinmost.lifx.model.LifxColor;
import com.kevinmost.lifx.model.LifxEntity;
import com.kevinmost.lifx.model.PowerState;
import com.kevinmost.lifx.model.Selector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import static com.kevinmost.internal.Util.assertRange;

@JsonAdapter(Operation.Adapter.class)
public final class Operation {

  @NotNull private final Selector selector;

  @Nullable private PowerState powerState;
  @Nullable private LifxColor color;
  @Nullable private Double brightness;
  @Nullable private Long durationSeconds;
  @Nullable private Double infraredBrightness;

  @NotNull public static Operation forSelector(@NotNull Selector selector) {
    return new Operation(selector);
  }

  @NotNull public static Operation forEntity(@NotNull LifxEntity entity) {
    return new Operation(entity.selector());
  }

  private Operation(@NotNull Selector selector) {
    this.selector = selector;
  }

  @NotNull public Operation setPowerState(@NotNull PowerState powerState) {
    this.powerState = powerState;
    return this;
  }

  @NotNull public Operation setColor(@NotNull LifxColor color) {
    this.color = color;
    return this;
  }

  @NotNull public Operation setBrightness(double brightness) {
    assertRange("brightness", brightness, 0.0, 1.0);
    this.brightness = brightness;
    return this;
  }

  @NotNull public Operation setDuration(long time, @NotNull TimeUnit unit) {
    final long durationSeconds = unit.toSeconds(time);
    assertRange("duration", durationSeconds, 0.0, TimeUnit.DAYS.toSeconds(100 * 365));
    this.durationSeconds = durationSeconds;
    return this;
  }

  @NotNull public Operation setInfraredBrightness(double infraredBrightness) {
    assertRange("infraredBrightness", infraredBrightness, 0.0, 1.0);
    this.infraredBrightness = infraredBrightness;
    return this;
  }

  static class Adapter implements JsonSerializer<Operation>, JsonDeserializer<Operation> {


    @Override
    public JsonElement serialize(Operation src, Type typeOfSrc, JsonSerializationContext context) {
      final JSONObjectBuilder builder = new JSONObjectBuilder()
          .add("selector", src.selector.toString());
      {
        final PowerState powerState = src.powerState;
        if (powerState != null) {
          builder.add("power", powerState == PowerState.ON ? "on" : "off");
        }
      }
      {
        final LifxColor color = src.color;
        if (color != null) {
          builder.add("color", color.toString());
        }
      }
      {
        final Double brightness = src.brightness;
        if (brightness != null) {
          builder.add("brightness", brightness);
        }
      }
      {
        final Long durationSeconds = src.durationSeconds;
        if (durationSeconds != null) {
          builder.add("duration", durationSeconds);
        }
      }
      {
        final Double infraredBrightness = src.infraredBrightness;
        if (infraredBrightness != null) {
          builder.add("infrared", infraredBrightness);
        }
      }
      return builder.build();
    }

    @Override public Operation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      final JsonObject root = json.getAsJsonObject();

      final PowerState powerState =
          root.has("power") ? JsonUtil.fromJSON(context, root.get("power"), PowerState.class) : null;
      final LifxColor color = root.has("color") ? JsonUtil.fromJSON(context, root.get("color"), LifxColor.class) : null;
      final Double brightness = root.has("brightness") ? root.get("brightness").getAsDouble() : null;
      final Long durationSeconds = root.has("duration") ? ((long) root.get("duration").getAsDouble()) : null;
      final Double infraredBrightness = root.has("infrared") ? root.get("infrared").getAsDouble() : null;

      final Operation op = Operation.forSelector(Selector.unsafe(root.get("selector").getAsString()));
      if (powerState != null) {
        op.setPowerState(powerState);
      }
      if (color != null) {
        op.setColor(color);
      }
      if (brightness != null) {
        op.setBrightness(brightness);
      }
      if (durationSeconds != null) {
        op.setDuration(durationSeconds, TimeUnit.SECONDS);
      }
      if (infraredBrightness != null) {
        op.setBrightness(infraredBrightness);
      }
      return op;
    }
  }
}
