package com.kevinmost.lifx.request;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.kevinmost.lifx.LifxRequest;
import com.kevinmost.lifx.model.LifxColor;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;

public final class VerifyColorRequest extends LifxRequest.Adapter<LifxColor> {

  @NotNull private final String value;

  VerifyColorRequest(@NotNull String value) {
    this.value = value;
  }

  @NotNull @Override public Request buildRequest(@NotNull HttpUrl baseURL, @NotNull Gson gson) {
    return new Request.Builder()
        .url(baseURL.newBuilder()
            .addPathSegments("v1/color")
            .addQueryParameter("string", value)
            .build())
        .get()
        .build();
  }

  @NotNull @Override public LifxColor unmarshal(@NotNull JsonElement json, @NotNull Gson gson) {
    return gson.fromJson(json, LifxColor.class);
  }
}
