package com.kevinmost.internal;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class Util {

  @NotNull private static final DecimalFormat TWO_DECIMAL_PLACES = new DecimalFormat("#.##");

  private Util() { throw new UnsupportedOperationException("No instances"); }

  public static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException("Interrupted!", e);
    }
  }

  public static double max(double... nums) {
    if (nums.length == 0) {
      throw new IllegalArgumentException("Util.max() cannot be called with an empty array");
    }
    double max = Integer.MIN_VALUE;
    for (final double num : nums) {
      max = Math.max(max, num);
    }
    return max;
  }

  public static double min(double... nums) {
    if (nums.length == 0) {
      throw new IllegalArgumentException("Util.max() cannot be called with an empty array");
    }
    double min = Integer.MAX_VALUE;
    for (final double num : nums) {
      min = Math.min(min, num);
    }
    return min;
  }

  public static double clamp(double value, double min, double max) {
    return (value < min) ? min
        : (value > max) ? max
            : value;
  }

  public static int round(double value) {
    return (int) Math.round(value);
  }

  public static double assertRange(String label, double value, double min, double max) {
    if (value < min || value > max) {
      throw new IllegalArgumentException(String.format("%s must be between %f and %f. Value was: %f",
          label,
          min,
          max,
          value
      ));
    }
    return value;
  }

  public static int assertRange(String label, int value, int min, int max) {
    if (value < min || value > max) {
      throw new IllegalArgumentException(String.format("%s must be between %d and %d. Value was: %d",
          label,
          min,
          max,
          value
      ));
    }
    return value;
  }

  public static int posModulo(int num, int mod) {
    final int result = num % mod;
    return result >= 0 ? result : result + mod;
  }

  public static double posModulo(double num, double mod) {
    final double result = num % mod;
    return result >= 0 ? result : result + mod;
  }

  public static double roundTo2Places(double in) {
    return Double.parseDouble(TWO_DECIMAL_PLACES.format(in));
  }
}
