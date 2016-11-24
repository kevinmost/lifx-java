package com.kevinmost.lifx.request;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.kevinmost.internal.JsonUtil;
import com.kevinmost.lifx.LifxRequest;
import com.kevinmost.lifx.model.Light;
import com.kevinmost.lifx.model.Selector;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ListLightsRequest extends LifxRequest.Adapter<List<Light>> {

//  @NotNull public static ListLightsRequest create() {
//    return create(Selector.ALL);
//  }
//
//  @NotNull public static ListLightsRequest create(@NotNull HasLifxID hasID) {
//    return create(hasID.selector());
//  }
//
//  @NotNull public static ListLightsRequest create(@NotNull Selector selector) {
//    return new ListLightsRequest(selector);
//  }

  @NotNull private final Selector selector;

  ListLightsRequest(@NotNull Selector selector) {
    this.selector = selector;
  }

  @NotNull @Override public Request buildRequest(@NotNull HttpUrl baseURL, @NotNull Gson gson) {
    return new Request.Builder()
        .url(baseURL.newBuilder()
            .addPathSegments("v1/lights").addPathSegment(selector.toString())
            .build())
        .get()
        .build();
  }

  @NotNull @Override public List<Light> unmarshal(@NotNull JsonElement json, @NotNull Gson gson) {
    return JsonUtil.fromJSON(gson, json, new TypeToken<List<Light>>() {});
  }
}
