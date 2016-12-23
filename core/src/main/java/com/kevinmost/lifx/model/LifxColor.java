package com.kevinmost.lifx.model;

import com.google.auto.value.AutoValue;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import com.kevinmost.internal.JsonUtil;
import com.kevinmost.internal.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.kevinmost.internal.Util.assertRange;
import static com.kevinmost.internal.Util.posModulo;

@JsonAdapter(LifxColor.Adapter.class)
@AutoValue
public abstract class LifxColor {

  private static final int RGB_MIN = 0;
  private static final int RGB_MAX = 255;

  @NotNull public static final LifxColor WHITE = hsv(null, 0.0, null);
  @NotNull public static final LifxColor RED = hsv(0.0, 1.0, null);
  @NotNull public static final LifxColor ORANGE = hsv(36.0, 1.0, null);
  @NotNull public static final LifxColor YELLOW = hsv(60.0, 1.0, null);
  @NotNull public static final LifxColor GREEN = hsv(120.0, 1.0, null);
  @NotNull public static final LifxColor CYAN = hsv(180.0, 1.0, null);
  @NotNull public static final LifxColor BLUE = hsv(250.0, 1.0, null);
  @NotNull public static final LifxColor PURPLE = hsv(280.0, 1.0, null);
  @NotNull public static final LifxColor PINK = hsv(325.0, 1.0, null);


  @NotNull public static LifxColor create() {
    return new AutoValue_LifxColor.Builder().build();
  }

  @NotNull public static LifxColor white(int kelvin) {
    return create().withKelvin(kelvin);
  }

  @NotNull public static LifxColor hsv(@Nullable Double h, @Nullable Double s, @Nullable Double v) {
    return create().withHue(h).withSaturation(s).withBrightness(v);
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

    return create()
        .withHue(Double.isNaN(hue) ? 0 : hue)
        .withSaturation(saturation)
        .withBrightness(brightness)
        ;
  }

  @Contract(pure = true) @NotNull public final LifxColor withHue(@Nullable Double hue) {
    if (hue == null) {
      return this;
    }
    assertRange("hue", hue, 0, 360);
    return toBuilder().hue(Util.round(hue, 13)).build();
  }

  @Contract(pure = true) @NotNull public final LifxColor withSaturation(@Nullable Double saturation) {
    if (saturation == null) {
      return this;
    }
    assertRange("saturation", saturation, 0, 1);
    return toBuilder().saturation(Util.round(saturation, 13)).build();
  }

  @Contract(pure = true) @NotNull public final LifxColor withBrightness(@Nullable Double brightness) {
    if (brightness == null) {
      return this;
    }
    assertRange("brightness", brightness, 0, 1);
    return toBuilder().brightness(Util.round(brightness, 13)).build();
  }

  @Contract(pure = true) @NotNull public final LifxColor withKelvin(@Nullable Integer kelvin) {
    if (kelvin == null) {
      return this;
    }
    assertRange("kelvin", kelvin, 2500, 9000);
    return toBuilder().kelvin(kelvin).build();
  }

  @Nullable public abstract Double hue();
  @Nullable public abstract Double saturation();
  @Nullable public abstract Double brightness();
  @Nullable public abstract Integer kelvin();

  @NotNull abstract Builder toBuilder();


  /**
   * The user should not be able to access this class; we need to validate the ranges of whatever values they put in,
   * and make sure that we coerce it to 13 decimal places (the highest precision we get back from the LiFX API)
   */
  @AutoValue.Builder
  static abstract class Builder {
    @NotNull abstract Builder hue(@Nullable Double hue);
    @NotNull abstract Builder saturation(@Nullable Double saturation);
    @NotNull abstract Builder brightness(@Nullable Double brightness);
    @NotNull abstract Builder kelvin(@Nullable Integer kelvin);
    @NotNull abstract LifxColor build();
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
      if (json.isJsonPrimitive()) {
        final String rawString = json.getAsJsonPrimitive().getAsString();
        final String[] splits = rawString.trim().split("\\s+");
        LifxColor color = LifxColor.create();
        for (final String split : splits) {
          final String value = split.substring(split.indexOf(':') + 1);
          if (split.startsWith("hue")) {
            color = color.withHue(Double.valueOf(value));
          } else if (split.startsWith("saturation")) {
            color = color.withSaturation(Double.valueOf(value));
          } else if (split.startsWith("brightness")) {
            color = color.withBrightness(Double.valueOf(value));
          } else if (split.startsWith("kelvin")) {
            color = color.withKelvin(Integer.parseInt(value));
          } else {
            throw new IllegalStateException("Unknown option in color-string: " + split);
          }
        }
        return color;
      } else {
        final JsonObject root = json.getAsJsonObject();
        LifxColor color = LifxColor.create();
        if (JsonUtil.notNull(root.get("hue"))) {
          color = color.withHue(root.get("hue").getAsDouble());
        }
        if (JsonUtil.notNull(root.get("saturation"))) {
          color = color.withSaturation(root.get("saturation").getAsDouble());
        }
        if (JsonUtil.notNull(root.get("brightness"))) {
          color = color.withBrightness(root.get("brightness").getAsDouble());
        }
        if (JsonUtil.notNull(root.get("kelvin"))) {
          color = color.withKelvin(root.get("kelvin").getAsInt());
        }
        return color;
      }
    }

    @Override public JsonElement serialize(LifxColor src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.toString());
    }
  }
}