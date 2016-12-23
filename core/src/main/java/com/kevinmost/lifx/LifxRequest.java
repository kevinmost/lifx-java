package com.kevinmost.lifx;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kevinmost.internal.JsonUtil;
import com.kevinmost.internal.Util;
import com.kevinmost.lifx.model.LifxError;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public interface LifxRequest<T> {
  @NotNull Request buildRequest(@NotNull HttpUrl baseURL, @NotNull Gson gson);
  @NotNull T unmarshal(@NotNull JsonElement json, @NotNull Gson gson);

  abstract class Adapter<T> implements LifxRequest<T> {
    @NotNull public final LifxResult<T> execute() {
      final LifxClient defaultInstance = LifxClientImpl.DEFAULT;
      if (defaultInstance == null) {
        throw new IllegalStateException(
            "Cannot call .execute() with the default LifxClient instance before calling LifxClient.Builder.buildAsDefault()"
        );
      }
      return execute(defaultInstance);
    }

    @NotNull public final LifxResult<T> execute(@NotNull LifxClient client) {
      LifxClientImpl clientImpl = (LifxClientImpl) client;
      final Response response;
      try {
        response = clientImpl.client.newCall(buildRequest(clientImpl.baseURL, clientImpl.gson)).execute();
      } catch (IOException e) {
        return new LifxResult.NetworkError<>(this, e);
      }
      final JsonElement json;
      {
        final String rawJSON;
        try {
          rawJSON = response.body().string();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        json = clientImpl.gson.fromJson(rawJSON, JsonElement.class);
      }
      final int httpCode = response.code();
      if (200 <= httpCode && httpCode < 300) {
        final T value = unmarshal(json, clientImpl.gson);
        return new LifxResult.Success<>(this, httpCode, value);
      }
      final List<LifxError> errors;
      if (json.isJsonObject()) {
        final JsonObject root = json.getAsJsonObject();
        errors = root.has("errors")
            ? Util.assertNotNull(JsonUtil.fromJSON(clientImpl.gson, root.get("errors"), new TypeToken<List<LifxError>>() {}))
            : Collections.<LifxError>emptyList();
      } else {
        errors = Collections.emptyList();
      }
      return new LifxResult.Failure<>(this, httpCode, errors);
    }
  }
}
