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
import com.kevinmost.internal.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

import static com.kevinmost.internal.Util.assertRange;
import static com.kevinmost.internal.Util.clamp;
import static com.kevinmost.internal.Util.posModulo;
import static com.kevinmost.internal.Util.round;
import static com.kevinmost.lifx.model.LifxColor.HSV.BRIGHTNESS_MAX;
import static com.kevinmost.lifx.model.LifxColor.HSV.BRIGHTNESS_MIN;
import static com.kevinmost.lifx.model.LifxColor.HSV.HUE_MAX;
import static com.kevinmost.lifx.model.LifxColor.HSV.HUE_MIN;
import static com.kevinmost.lifx.model.LifxColor.HSV.SATURATION_MAX;
import static com.kevinmost.lifx.model.LifxColor.HSV.SATURATION_MIN;
import static com.kevinmost.lifx.model.LifxColor.RGB.RGB_MAX;
import static com.kevinmost.lifx.model.LifxColor.RGB.RGB_MIN;
import static com.kevinmost.lifx.model.LifxColor.White.KELVIN_MAX;
import static com.kevinmost.lifx.model.LifxColor.White.KELVIN_MIN;

@JsonAdapter(LifxColor.Adapter.class)
public abstract class LifxColor {

  @NotNull public static White createWhite(int kelvin) {
    Util.assertRange("kelvin", kelvin, KELVIN_MIN, KELVIN_MAX);
    return new AutoValue_LifxColor_White(kelvin);
  }

  @NotNull
  public static RGB createRGB(int r, int g, int b) {
    assertRange("r", r, RGB_MIN, RGB_MAX);
    assertRange("g", g, RGB_MIN, RGB_MAX);
    assertRange("b", b, RGB_MIN, RGB_MAX);
    return new AutoValue_LifxColor_RGB(r, g, b);
  }

  @NotNull public static HSV createHSV(int hue, double saturation, double brightness) {
    assertRange("hue", hue, HUE_MIN, HUE_MAX);
    assertRange("saturation", saturation, SATURATION_MIN, SATURATION_MAX);
    assertRange("brightness", brightness, BRIGHTNESS_MIN, BRIGHTNESS_MAX);
    // LiFX API only deals with these decimals to 2 places
    return new AutoValue_LifxColor_HSV(hue, Util.roundTo2Places(saturation), Util.roundTo2Places(brightness));
  }

  @NotNull public abstract String toString();

  @NotNull public abstract HSV toHSV();

  @AutoValue
  @JsonAdapter(White.Adapter.class)
  public static abstract class White extends LifxColor {

    public static final int KELVIN_MAX = 9000;
    public static final int KELVIN_MIN = 2500;

    /**
     * @return an approximation of this color temperature in RGB
     * WARNING: The generated RGB value is only accurate between 1000K-40000K
     * Algorithm credit: http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/
     */
    @NotNull public final RGB toRGB() {
      final int temp = kelvin() / 100;

      final int red;
      if (temp <= 66) {
        red = 255;
      } else {
        red = round(clamp(329.698727446 * Math.pow(temp - 60, -0.1332047592), 0, 255));
      }

      final int green;
      if (temp <= 66) {
        green = round(clamp(99.4708025861 * Math.log(temp) - 161.1195681661, 0, 255));
      } else {
        green = round(clamp(288.1221695283 * Math.pow(temp - 60, -0.0755148492), 0, 255));
      }

      final int blue;
      if (temp >= 66) {
        blue = 255;
      } else if (temp <= 19) {
        blue = 0;
      } else {
        blue = round(clamp(138.5177312231 * Math.log(temp - 10) - 305.0447927307, 0, 255));
      }
      return createRGB(red, green, blue);
    }

    /**
     * @return an approximation of this color temperature in HSV
     * WARNING: The generated RGB value is only accurate between 1000K-40000K
     * Algorithm credit: http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/
     */
    @Override @NotNull public final HSV toHSV() {
      return toRGB().toHSV();
    }

    /**
     * @return the temperature of this shade of white, in kelvin
     */
    public abstract int kelvin();

    White() {} // AutoValue instances only

    @NotNull @Override public String toString() {
      return "kelvin:" + kelvin();
    }

    final class Adapter implements JsonDeserializer<White>, JsonSerializer<White> {
      @Override public JsonElement serialize(White src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
      }

      @Override
      public White deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        final int kelvin;
        if (json.isJsonObject()) {
          kelvin = (int) json.getAsJsonObject().get("kelvin").getAsDouble();
        } else {
          kelvin = (int) Double.parseDouble(json.getAsString().replace("kelvin:", ""));
        }
        return createWhite(kelvin);
      }
    }
  }


  @AutoValue
  @JsonAdapter(RGB.Adapter.class)
  public static abstract class RGB extends LifxColor {

    public static final int RGB_MIN = 0;
    public static final int RGB_MAX = 255;

    @Override @NotNull public final HSV toHSV() {
      final double rPrime = r() / 255.0;
      final double gPrime = g() / 255.0;
      final double bPrime = b() / 255.0;

      final double cMax = Util.max(rPrime, gPrime, bPrime);
      final double cMin = Util.min(rPrime, gPrime, bPrime);
      final double delta = cMax - cMin;

      final int hue;
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
        hue = round(posModulo(60 * huePrime, 360.0));
      }

      final double saturation = (cMax == 0) ? 0 : (delta / cMax);

      //noinspection UnnecessaryLocalVariable
      final double brightness = cMax;

      return createHSV(hue, saturation, brightness);
    }

    /**
     * @return the red component of this color, from 0 - 255
     */
    public abstract int r();
    /**
     * @return the green component of this color, from 0 - 255
     */
    public abstract int g();
    /**
     * @return the blue component of this color, from 0 - 255
     */
    public abstract int b();

    RGB() {} // AutoValue instances only

    @NotNull @Override public String toString() {
      return String.format("rgb:%d,%d,%d", r(), g(), b());
    }

    static class Adapter implements JsonSerializer<RGB>, JsonDeserializer<RGB> {
      @Override public JsonElement serialize(RGB src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
      }

      @Override public RGB deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        final String string = json.getAsString();
        final String[] components = string.substring(string.indexOf(':') + 1).split(",");
        final int r = Integer.parseInt(components[0]);
        final int g = Integer.parseInt(components[1]);
        final int b = Integer.parseInt(components[2]);
        return createRGB(r, g, b);
      }
    }
  }


  @AutoValue
  @JsonAdapter(HSV.Adapter.class)
  public static abstract class HSV extends LifxColor {

    public static final int HUE_MIN = 0;
    public static final int HUE_MAX = 360;
    public static final double SATURATION_MIN = 0.0;
    public static final double SATURATION_MAX = 1.0;
    public static final double BRIGHTNESS_MIN = 0.0;
    public static final double BRIGHTNESS_MAX = 1.0;


    /**
     * @return an RGB approximation of this color. WARNING: RGB is a poor approximation of the HSV color space, so
     * this color may not be accurate, and converting the result back to HSV may not give you the same color you
     * started with
     */
    @NotNull public final RGB toRGB() {
      final double chroma = saturation() * brightness();

      final double huePrime = hue() / 60.0;

      final double x = chroma * (1 - Math.abs(huePrime % 2 - 1));

      final double r1;
      final double g1;
      final double b1;
      if (huePrime < 0) {
        throw new IllegalArgumentException("Hue cannot be negative");
      } else if (huePrime < 1) {
        r1 = chroma;
        g1 = x;
        b1 = 0;
      } else if (huePrime < 2) {
        r1 = x;
        g1 = chroma;
        b1 = 0;
      } else if (huePrime < 3) {
        r1 = 0;
        g1 = chroma;
        b1 = x;
      } else if (huePrime < 4) {
        r1 = 0;
        g1 = x;
        b1 = chroma;
      } else if (huePrime < 5) {
        r1 = x;
        g1 = 0;
        b1 = chroma;
      } else if (huePrime < 6) {
        r1 = chroma;
        g1 = 0;
        b1 = x;
      } else {
        throw new IllegalArgumentException("Hue must be between 0-360");
      }

      final double m = brightness() - chroma;

      final double r = r1 + m;
      final double g = g1 + m;
      final double b = b1 + m;

      return createRGB(round(255 * r), round(255 * g), round(255 * b));
    }

    @NotNull @Override public HSV toHSV() {
      return this;
    }

    /**
     * @return the hue of this color, from 0 - 360
     */
    public abstract int hue();

    /**
     * @return the saturation of this color, from 0.0 - 1.0
     */
    public abstract double saturation();

    /**
     * @return the brightness of this color, from 0.0 - 1.0
     */
    public abstract double brightness();

    HSV() {} // AutoValue instances only

    @NotNull @Override public String toString() {
      return String.format("hue:%d saturation:%.2f brightness:%.2f", hue(), saturation(), brightness());
    }

    static class Adapter implements JsonSerializer<HSV>, JsonDeserializer<HSV> {
      @Override public HSV deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        final JsonObject root = json.getAsJsonObject();

        @Nullable final JsonObject colorJSON = root.getAsJsonObject("color");

        final JsonObject hueAndSaturationJSON = colorJSON != null ? colorJSON : root;
        final int hue = round(hueAndSaturationJSON.get("hue").getAsDouble());
        final double saturation = hueAndSaturationJSON.get("saturation").getAsDouble();

        final double brightness;
        if (root.has("brightness")) {
          brightness = root.get("brightness").getAsDouble();
        } else {
          if (colorJSON != null) {
            brightness = colorJSON.get("brightness").getAsDouble();
          } else {
            throw new IllegalArgumentException("There is no brightness in this color JSON. JSON: " + json);
          }
        }
        return createHSV(hue, saturation, brightness);
      }

      @Override public JsonElement serialize(HSV src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
      }
    }
  }


  final class Adapter implements JsonSerializer<LifxColor>, JsonDeserializer<LifxColor> {
    @Override public LifxColor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      final Class<?> type;
      if (json.isJsonObject()) {
        final JsonObject root = json.getAsJsonObject();
        if (root.has("kelvin") && !root.get("kelvin").isJsonNull()) {
          type = White.class;
        } else {
          type = HSV.class;
        }
      } else {
        final String string = json.getAsJsonPrimitive().getAsString();
        if (string.contains("kelvin")) {
          type = White.class;
        } else if (string.contains("hue")) {
          type = HSV.class;
        } else if (string.contains("rgb") || string.startsWith("#")) {
          type = RGB.class;
        } else {
          throw new IllegalArgumentException("Unknown LifxColor: " + json);
        }
      }
      return context.deserialize(json, type);
    }

    @Override public JsonElement serialize(LifxColor src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src, src.getClass());
    }
  }
}