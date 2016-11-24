package com.kevinmost.lifx.model;

import com.google.auto.value.AutoValue;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

@AutoValue
@JsonAdapter(LifxLocation.Adapter.class)
public abstract class LifxLocation implements LifxEntity {

  @NotNull public abstract String name();

  @NotNull @Override public final Selector selector() {
    return Selector.forLocation(this);
  }

  LifxLocation() {} // AutoValue instances only

  static class Adapter implements JsonDeserializer<LifxLocation> {
    @Override public LifxLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      final JsonObject root = json.getAsJsonObject();
      return new AutoValue_LifxLocation(
          root.get("id").getAsString(),
          root.get("name").getAsString()
      );
    }
  }
}