package com.kevinmost.lifx.model;

import com.google.auto.value.AutoValue;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import com.kevinmost.auto.value.custom_hashcode_equals.adapter.IgnoreForHashCodeEquals;
import com.kevinmost.internal.JsonUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.UUID;

@AutoValue
@JsonAdapter(Light.Adapter.class)
public abstract class Light implements LifxEntity {

  @NotNull @Override public final Selector selector() {
    return Selector.forLight(this);
  }

  @NotNull public abstract UUID uuid();
  @Nullable public abstract String label();
  public abstract boolean connected();
  public abstract PowerState powerState();
  @NotNull public abstract LifxColor color();
  @Nullable public abstract Double infrared();
  public abstract double brightness();
  @NotNull public abstract LifxGroup group();
  @NotNull public abstract LifxLocation location();
  @IgnoreForHashCodeEquals @NotNull public abstract Date lastSeen();
  @IgnoreForHashCodeEquals public abstract double secondsSinceSeen();
  @NotNull public abstract LifxProduct product();


  Light() {} // AutoValue instances only

  static class Adapter implements JsonDeserializer<Light> {
    @Override public Light deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      final JsonObject root = json.getAsJsonObject();
      final Double infrared = JsonUtil.notNull(root.get("infrared"))
          ? Double.parseDouble(root.get("infrared").getAsString())
          : null;
      return new AutoValue_Light(
          root.get("id").getAsString(),
          UUID.fromString(root.get("uuid").getAsString()),
          root.get("label").getAsString(),
          root.get("connected").getAsBoolean(),
          JsonUtil.fromJSON(context, root.get("power"), PowerState.class),
          JsonUtil.fromJSON(context, root.get("color"), LifxColor.class),
          infrared,
          root.get("brightness").getAsDouble(),
          JsonUtil.fromJSON(context, root.get("group"), LifxGroup.class),
          JsonUtil.fromJSON(context, root.get("location"), LifxLocation.class),
          JsonUtil.fromJSON(context, root.get("last_seen"), Date.class),
          root.get("seconds_since_seen").getAsDouble(),
          JsonUtil.fromJSON(context, root.get("product"), LifxProduct.class)
      );
    }
  }
}