package com.kevinmost.internal;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Util {

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

  @Contract("null -> fail") @NotNull public static <T> T assertNotNull(@Nullable T in) {
    if (in == null) {
      throw new NullPointerException();
    }
    return in;
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

  @Contract("null, _ -> null") public static Double round(@Nullable Double in, int places) {
    if (in == null) {
      return null;
    }
    return new BigDecimal(in)
        .setScale(places, RoundingMode.HALF_EVEN)
        .doubleValue();
  }

  @NotNull public static String joinToString(@NotNull Iterable<String> strings, @NotNull String join) {
    final StringBuilder sb = new StringBuilder();
    for (final String string : strings) {
      sb.append(string).append(join);
    }
    sb.setLength(sb.length() - join.length());
    return sb.toString();
  }

  @NotNull public static <T> List<T> filter(@NotNull Iterable<T> in, @NotNull Func1<T, Boolean> predicate) {
    final List<T> out = new ArrayList<>();
    for (final T element : in) {
      if (predicate.call(element)) {
        out.add(element);
      }
    }
    return out;
  }
}
