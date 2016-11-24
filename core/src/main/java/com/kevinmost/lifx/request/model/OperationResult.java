package com.kevinmost.lifx.request.model;

import com.google.auto.value.AutoValue;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import com.kevinmost.internal.JsonUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.List;

@AutoValue
@JsonAdapter(OperationResult.Adapter.class)
public abstract class OperationResult {

  @NotNull public abstract Operation operation();
  @NotNull public abstract List<SetLightResult> results();

  OperationResult() {} // AutoValue instances only

  static class Adapter implements JsonDeserializer<OperationResult> {

    @Override
    public OperationResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      final JsonObject root = json.getAsJsonObject();
      return new AutoValue_OperationResult(
          JsonUtil.fromJSON(context, root.get("operation"), Operation.class),
          JsonUtil.fromJSON(context, root.get("results"), new TypeToken<List<SetLightResult>>() {})
      );
    }
  }
}
