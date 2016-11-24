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
@JsonAdapter(LifxGroup.Adapter.class)
public abstract class LifxGroup implements LifxEntity {

  @NotNull public abstract String name();

  @NotNull @Override public final Selector selector() {
    return Selector.forGroup(this);
  }

  LifxGroup() {} // AutoValue instances only

  static class Adapter implements JsonDeserializer<LifxGroup> {
    @Override public LifxGroup deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      final JsonObject root = json.getAsJsonObject();
      return new AutoValue_LifxGroup(
          root.get("id").getAsString(),
          root.get("name").getAsString()
      );
    }
  }
}