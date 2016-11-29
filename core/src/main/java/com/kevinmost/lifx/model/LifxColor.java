package com.kevinmost.lifx.model;

import com.google.auto.value.AutoValue;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import com.kevinmost.internal.JSONObjectBuilder;
import com.kevinmost.internal.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.kevinmost.internal.JsonUtil.notNull;
import static com.kevinmost.internal.Util.assertRange;
import static com.kevinmost.internal.Util.posModulo;

@JsonAdapter(LifxColor.Adapter.class)
@AutoValue
public abstract class LifxColor {

  public static final int RGB_MIN = 0;
  public static final int RGB_MAX = 255;

  @NotNull public static Builder builder() {
    return new AutoValue_LifxColor.Builder();
  }

  @NotNull public static LifxColor white(int kelvin) {
    return builder().kelvin(kelvin).build();
  }

  @NotNull public static LifxColor rgb(int r, int g, int b) {
    assertRange("r", r, RGB_MIN, RGB_MAX);
    assertRange("g", g, RGB_MIN, RGB_MAX);
    assertRange("b", b, RGB_MIN, RGB_MAX);
    final double rPrime = r / 255.0;
    final double gPrime = g / 255.0;
    final double bPrime = b / 255.0;

    final double cMax = Util.max(rPrime, gPrime, bPrime);
    final double cMin = Util.min(rPrime, gPrime, bPrime);
    final double delta = cMax - cMin;

    final double hue;
    {
      final double huePrime;
      if (cMax == rPrime) {
        huePrime = ((gPrime - bPrime) / delta) % 6;
      } else if (cMax == gPrime) {
        huePrime = ((bPrime - rPrime) / delta) + 2;
      } else if (cMax == bPrime) {
        huePrime = ((rPrime - gPrime) / delta) + 4;
      } else {
        throw new AssertionError("cMax must be equal to either rPrime, gPrime, or bPrime");
      }
      hue = posModulo(60 * huePrime, 360.0);
    }

    final double saturation = (cMax == 0) ? 0 : (delta / cMax);

    //noinspection UnnecessaryLocalVariable
    final double brightness = cMax;

    return builder()
        .hue(Double.isNaN(hue) ? 0 : hue)
        .brightness(brightness)
        .saturation(saturation)
        .build();
  }

  @NotNull public abstract LifxColor withHue(@Nullable Double hue);
  @NotNull public abstract LifxColor withSaturation(@Nullable Double saturation);
  @NotNull public abstract LifxColor withBrightness(@Nullable Double brightness);
  @NotNull public abstract LifxColor withKelvin(@Nullable Integer kelvin);

  @Nullable public abstract Double hue();
  @Nullable public abstract Double saturation();
  @Nullable public abstract Double brightness();
  @Nullable public abstract Integer kelvin();

  @AutoValue.Builder
  public static abstract class Builder {
    @NotNull public abstract Builder hue(@Nullable Double hue);
    @NotNull public abstract Builder saturation(@Nullable Double saturation);
    @NotNull public abstract Builder brightness(@Nullable Double brightness);
    @NotNull public abstract Builder kelvin(@Nullable Integer kelvin);
    @NotNull abstract LifxColor autoBuild();

    @NotNull public final LifxColor build() {
      final LifxColor color = autoBuild();
      {
        final Double hue = color.hue();
        if (hue != null) {
          assertRange("hue", hue, 0.0, 360.0);
        }
      }
      {
        final Double saturation = color.saturation();
        if (saturation != null) {
          assertRange("saturation", saturation, 0.0, 1.0);
        }
      }
      {
        final Double brightness = color.brightness();
        if (brightness != null) {
          assertRange("brightness", brightness, 0.0, 1.0);
        }
      }
      {
        final Integer kelvin = color.kelvin();
        if (kelvin != null) {
          assertRange("kelvin", kelvin, 2500, 9000);
        }
      }
      return color;
    }
  }

  @Override public String toString() {
    final List<String> strings = new ArrayList<>();
    {
      final Double hue = hue();
      if (hue != null) {
        strings.add("hue:" + hue);
      }
    }
    {
      final Double saturation = saturation();
      if (saturation != null) {
        strings.add("saturation:" + saturation);
      }
    }
    {
      final Double brightness = brightness();
      if (brightness != null) {
        strings.add("brightness:" + brightness);
      }
    }
    {
      final Integer kelvin = kelvin();
      if (kelvin != null) {
        strings.add("kelvin:" + kelvin);
      }
    }
    return Util.joinToString(strings, " ");
  }

  LifxColor() {} // AutoValue instances only

  static class Adapter implements JsonSerializer<LifxColor>, JsonDeserializer<LifxColor> {

    @Override public LifxColor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
        final String string = json.getAsJsonPrimitive().getAsString();
        if (string.contains("rgb")) {
          final String[] components = string.substring(string.indexOf(':') + 1).split(",");
          final int r = Integer.parseInt(components[0]);
          final int g = Integer.parseInt(components[1]);
          final int b = Integer.parseInt(components[2]);
          return rgb(r, g, b);
        }
      }
      final JsonElement hue;
      final JsonElement saturation;
      final JsonElement brightness;
      final JsonElement kelvin;
      {
        final JsonObject root = json.getAsJsonObject();
        hue = root.get("hue");
        saturation = root.get("saturation");
        brightness = root.get("brightness");
        kelvin = root.get("kelvin");
      }
      return builder()
          .hue(notNull(hue) ? hue.getAsDouble() : null)
          .saturation(notNull(saturation) ? saturation.getAsDouble() : null)
          .brightness(notNull(brightness) ? brightness.getAsDouble() : null)
          .kelvin(notNull(kelvin) ? kelvin.getAsInt() : null)
          .build();
    }

    @Override public JsonElement serialize(LifxColor src, Type typeOfSrc, JsonSerializationContext context) {
      return new JSONObjectBuilder()
          .add("hue", src.hue())
          .add("saturation", src.saturation())
          .add("kelvin", src.kelvin())
          .add("brightness", src.brightness())
          .build();
    }
  }
}