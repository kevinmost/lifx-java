package com.kevinmost.lifx.request.model;

import com.google.auto.value.AutoValue;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import com.kevinmost.internal.Func2;
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

@AutoValue
@JsonAdapter(Operation.Adapter.class)
public abstract class Operation {

  @NotNull public static Operation.Builder forEntity(@NotNull LifxEntity entity) {
    return forSelector(entity.selector());
  }

  @NotNull public static Operation.Builder forSelector(@NotNull Selector selector) {
    return new AutoValue_Operation.Builder().selector(selector);
  }

  @NotNull public abstract Selector selector();
  @Nullable public abstract PowerState powerState();
  @Nullable public abstract LifxColor color();
  @Nullable public abstract Double brightness();
  @Nullable public abstract Double infraredBrightness();

  @Nullable public final Long durationIn(@NotNull TimeUnit unit) {
    final Double inSeconds = duration();
    if (inSeconds == null) {
      return null;
    }
    return unit.convert(inSeconds.longValue(), TimeUnit.SECONDS);
  }

  @Nullable abstract Double duration();

  @AutoValue.Builder
  public static abstract class Builder {
    @NotNull public final Builder selector(@NotNull LifxEntity entity) {
      return selector(entity.selector());
    }

    @NotNull public abstract Builder selector(@NotNull Selector selector);

    @NotNull public abstract Builder powerState(@Nullable PowerState powerState);
    @NotNull public abstract Builder color(@Nullable LifxColor color);
    @NotNull public abstract Builder brightness(@Nullable Double brightness);
    @NotNull public abstract Builder infraredBrightness(@Nullable Double infraredBrightness);

    @NotNull public final Builder duration(long value, @NotNull TimeUnit unit) {
      duration((double) TimeUnit.SECONDS.convert(value, unit));
      return this;
    }

    @NotNull abstract Builder duration(@Nullable Double duration);

    @NotNull abstract Operation autoBuild();

    @NotNull public final Operation build() {
      final Operation operation = autoBuild();
      {
        final Double brightness = operation.brightness();
        if (brightness != null) {
          assertRange("brightness", brightness, 0.0, 1.0);
        }
      }
      {
        final Double infraredBrightness = operation.infraredBrightness();
        if (infraredBrightness != null) {
          assertRange("infraredBrightness", infraredBrightness, 0.0, 1.0);
        }
      }
      {
        final Long seconds = operation.durationIn(TimeUnit.SECONDS);
        if (seconds != null) {
          assertRange("duration", seconds, 0, TimeUnit.SECONDS.convert(10 * 365, TimeUnit.DAYS));
        }
      }
      return operation;
    }
  }

  Operation() {} // AutoValue instances only

  static class Adapter implements JsonSerializer<Operation>, JsonDeserializer<Operation> {

    @Override
    public JsonElement serialize(Operation src, Type typeOfSrc, JsonSerializationContext context) {
      final LifxColor color = src.color();
      final PowerState powerState = src.powerState();
      final JsonObject unfiltered = new JSONObjectBuilder()
          .add("selector", src.selector().toString())
          .add("power", powerState == null ? null : powerState == PowerState.ON ? "on" : "off")
          .add("color", color == null ? null : color.toString())
          .add("brightness", src.brightness())
          .add("infrared", src.infraredBrightness())
          .add("duration", src.durationIn(TimeUnit.SECONDS))
          .build();
      return JsonUtil.filter(unfiltered, new Func2<String, JsonElement, Boolean>() {
        @NotNull @Override public Boolean call(@NotNull String key, @NotNull JsonElement value) {
          return !value.isJsonNull();
        }
      });
    }

    @Override public Operation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      final JsonObject root = json.getAsJsonObject();

      final PowerState powerState = JsonUtil.fromJSON(context, root.get("power"), PowerState.class);
      final LifxColor color = JsonUtil.fromJSON(context, root.get("color"), LifxColor.class);
      final Double brightness = root.has("brightness") ? root.get("brightness").getAsDouble() : null;
      final Double durationSeconds = root.has("duration") ? root.get("duration").getAsDouble() : null;
      final Double infraredBrightness = root.has("infrared") ? root.get("infrared").getAsDouble() : null;

      return new AutoValue_Operation.Builder()
          .selector(Selector.unsafe(root.get("selector").getAsString()))
          .powerState(powerState)
          .color(color)
          .brightness(brightness)
          .duration(durationSeconds)
          .infraredBrightness(infraredBrightness)
          .build();
    }
  }
}
