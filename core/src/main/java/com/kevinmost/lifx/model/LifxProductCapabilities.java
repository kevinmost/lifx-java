package com.kevinmost.lifx.model;

import com.google.auto.value.AutoValue;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

@SuppressWarnings("NullableProblems")
@AutoValue
@JsonAdapter(LifxProductCapabilities.Adapter.class)
public abstract class LifxProductCapabilities {

  @NotNull public static Builder builder() {
    return new AutoValue_LifxProductCapabilities.Builder();
  }

  public abstract boolean hasColor();
  public abstract boolean hasVariableColorTemp();
  public abstract boolean hasIR();
  public abstract boolean hasMultizone();

  @AutoValue.Builder
  public interface Builder {
    @NotNull Builder hasColor(boolean hasColor);
    @NotNull Builder hasVariableColorTemp(boolean hasVariableColorTemp);
    @NotNull Builder hasIR(boolean hasIR);
    @NotNull Builder hasMultizone(boolean hasMultizone);
    @NotNull LifxProductCapabilities build();
  }

  LifxProductCapabilities() {} // AutoValue instances only

  static class Adapter implements JsonDeserializer<LifxProductCapabilities> {
    @Override
    public LifxProductCapabilities deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      final JsonObject root = json.getAsJsonObject();
      return builder()
          .hasColor(root.get("has_color").getAsBoolean())
          .hasVariableColorTemp(root.get("has_variable_color_temp").getAsBoolean())
          .hasIR(root.get("has_ir").getAsBoolean())
          .hasMultizone(root.get("has_multizone").getAsBoolean())
          .build();
    }
  }
}