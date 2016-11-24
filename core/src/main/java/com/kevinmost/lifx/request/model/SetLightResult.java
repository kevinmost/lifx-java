package com.kevinmost.lifx.request.model;

import com.google.auto.value.AutoValue;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

@AutoValue
@JsonAdapter(SetLightResult.Adapter.class)
public abstract class SetLightResult {

  @NotNull public static SetLightResult create() {
    throw new RuntimeException("TODO"); // TODO
  }

  @NotNull public abstract String id();
  @Nullable public abstract String label();
  @NotNull public abstract String status(); // what is this besides just "ok" all the time? We need to know but the documentation says nothing.

  SetLightResult() {} // AutoValue instances only

  static class Adapter implements JsonDeserializer<SetLightResult> {

    @Override
    public SetLightResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      final JsonObject root = json.getAsJsonObject();
      final String label;
      if (root.has("label")) {
        label = root.get("label").getAsString();
      } else {
        label = null;
      }
      return new AutoValue_SetLightResult(
          root.get("id").getAsString(),
          label,
          root.get("status").getAsString()
      );
    }
  }
}
