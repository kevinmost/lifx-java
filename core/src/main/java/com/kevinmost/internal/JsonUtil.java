package com.kevinmost.internal;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

  @NotNull public static <T> T fromJSON(@NotNull Gson gson, @NotNull JsonElement json, @NotNull Class<T> type) {
    return gson.fromJson(json, type);
  }

  @NotNull public static <T> T fromJSON(@NotNull Gson gson, @NotNull JsonElement json, @NotNull TypeToken<T> type) {
    return gson.fromJson(json, type.getType());
  }

  @NotNull
  public static <T> T fromJSON(
      @NotNull JsonDeserializationContext gson,
      @NotNull JsonElement json,
      @NotNull Class<T> type
  ) {
    return gson.deserialize(json, type);
  }

  @NotNull
  public static <T> T fromJSON(
      @NotNull JsonDeserializationContext gson,
      @NotNull JsonElement json,
      @NotNull TypeToken<T> type
  ) {
    return gson.deserialize(json, type.getType());
  }

  public static boolean notNull(@Nullable JsonElement json) {
    return json != null && !json.isJsonNull();
  }
}
