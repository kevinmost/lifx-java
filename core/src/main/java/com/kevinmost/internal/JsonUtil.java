package com.kevinmost.internal;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class JsonUtil {

  @NotNull private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf8");

  private JsonUtil() { throw new UnsupportedOperationException("No instances"); }

  @NotNull public static RequestBody toRequestBody(@NotNull Gson gson, @NotNull JsonElement json) {
    return RequestBody.create(MEDIA_TYPE_JSON, gson.toJson(json));
  }

  @NotNull public static <T> JsonElement toJSON(@NotNull Gson gson, @NotNull T src, @NotNull Class<T> type) {
    return gson.toJsonTree(src, type);
  }

  @NotNull
  public static <T> JsonElement toJSON(@NotNull Gson gson, @NotNull T src, @NotNull TypeToken<T> type) {
    return gson.toJsonTree(src, type.getType());
  }

  @Nullable public static <T> T fromJSON(@NotNull Gson gson, @Nullable JsonElement json, @NotNull Class<T> type) {
    return gson.fromJson(json, type);
  }

  @Nullable public static <T> T fromJSON(@NotNull Gson gson, @Nullable JsonElement json, @NotNull TypeToken<T> type) {
    return gson.fromJson(json, type.getType());
  }

  @Nullable
  public static <T> T fromJSON(
      @NotNull JsonDeserializationContext gson,
      @Nullable JsonElement json,
      @NotNull Class<T> type
  ) {
    return gson.deserialize(json, type);
  }

  @Nullable
  public static <T> T fromJSON(
      @NotNull JsonDeserializationContext gson,
      @Nullable JsonElement json,
      @NotNull TypeToken<T> type
  ) {
    return gson.deserialize(json, type.getType());
  }

  @NotNull public static <J extends JsonElement> J deepCopy(@NotNull J in) {
    final JsonElement out;
    if (in.isJsonNull()) {
      out = JsonNull.INSTANCE;
    } else if (in.isJsonPrimitive()) {
      final JsonPrimitive primitive = in.getAsJsonPrimitive();
      out = primitive.isNumber() ? new JsonPrimitive(primitive.getAsNumber())
          : primitive.isBoolean() ? new JsonPrimitive(primitive.getAsBoolean())
              : new JsonPrimitive(primitive.getAsString());
    } else if (in.isJsonArray()) {
      out = new JSONArrayBuilder()
          .addAll(in.getAsJsonArray(), new Func1<JsonElement, JsonElement>() {
            @NotNull @Override public JsonElement call(@NotNull JsonElement in) {
              return deepCopy(in);
            }
          }).build();
    } else {
      final JsonObject outObj = new JsonObject();
      for (final Map.Entry<String, JsonElement> entry : in.getAsJsonObject().entrySet()) {
        outObj.add(entry.getKey(), entry.getValue());
      }
      out = outObj;
    }
    @SuppressWarnings("unchecked") final J outJ = ((J) out);
    return outJ;
  }

  @NotNull
  public static JsonObject filter(@NotNull JsonObject in, @NotNull Func2<String, JsonElement, Boolean> predicate) {
    final JsonObject out = new JsonObject();
    for (final Map.Entry<String, JsonElement> entry : in.entrySet()) {
      if (predicate.call(entry.getKey(), entry.getValue())) {
        out.add(entry.getKey(), entry.getValue());
      }
    }
    return out;
  }

  @NotNull
  public static JsonArray filter(@NotNull JsonArray in, @NotNull Func2<Integer, JsonElement, Boolean> predicate) {
    final JsonArray out = new JsonArray();
    for (int i = 0; i < in.size(); i++) {
      final JsonElement element = in.get(i);
      if (predicate.call(i, element)) {
        out.add(element);
      }
    }
    return out;
  }

  public static boolean notNull(@Nullable JsonElement json) {
    return json != null && !json.isJsonNull();
  }
}
