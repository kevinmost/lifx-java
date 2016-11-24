package com.kevinmost.lifx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kevinmost.internal.AutoValueTypeAdapterFactory;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

final class LifxClientImpl implements LifxClient {

  @Nullable static LifxClient DEFAULT = null;

  @NotNull final String accessToken;

  @NotNull final HttpUrl baseURL;
  @NotNull final OkHttpClient client;

  @NotNull final Gson gson;

  LifxClientImpl(@NotNull Builder builder) {
    accessToken = builder.accessToken;
    baseURL = builder.baseURL;
    client = builder.client.newBuilder()
        .addInterceptor(new Interceptor() {
          @Override public Response intercept(Chain chain) throws IOException {
            return chain.proceed(chain.request().newBuilder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .build()
            );
          }
        })
        .build();
    gson = new GsonBuilder()
        .registerTypeAdapterFactory(new AutoValueTypeAdapterFactory())
        .create();
  }
}
