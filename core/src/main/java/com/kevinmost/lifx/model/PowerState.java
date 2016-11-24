package com.kevinmost.lifx.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(PowerState.Adapter.class)
public enum PowerState {
  ON,
  OFF;


  static class Adapter implements JsonSerializer<PowerState>, JsonDeserializer<PowerState> {

    @Override public PowerState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      final String str;
      if (json.isJsonObject()) {
        final JsonObject object = json.getAsJsonObject();
        if (object.has("power")) {
          str = object.get("power").getAsString();
        } else {
          return null;
        }
      } else if (json.isJsonPrimitive()) {
        str = json.getAsString();
      } else {
        throw new JsonParseException("Can't parse a " + json.getClass() + " to a " + PowerState.class.getName());
      }
      return str.equals("on") ? ON : OFF;
    }

    @Override public JsonElement serialize(PowerState src, Type typeOfSrc, JsonSerializationContext context) {
      if (src == null) {
        return JsonNull.INSTANCE;
      }
      return new JsonPrimitive(src == ON ? "on" : "off");
    }
  }
}
