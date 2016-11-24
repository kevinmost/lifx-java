package com.kevinmost.lifx;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;


public interface LifxClient {

  class Builder {
    @NotNull final String accessToken;
    @NotNull OkHttpClient client = new OkHttpClient();
    @NotNull HttpUrl baseURL = HttpUrl.parse("https://api.lifx.com");

    public Builder(@NotNull String accessToken) {
      this.accessToken = accessToken;
    }

    @NotNull public Builder baseURL(@NotNull HttpUrl baseURL) {
      this.baseURL = baseURL;
      return this;
    }

    @NotNull public Builder client(@NotNull OkHttpClient client) {
      this.client = client;
      return this;
    }

    @NotNull public final LifxClient build() {
      return new LifxClientImpl(this);
    }

    /**
     * builds this API client instance, and sets it as the default API client instance when executing requests with no
     * parameter
     */
    @NotNull public synchronized final void buildAsDefault() {
      LifxClientImpl.DEFAULT = build();
    }
  }
}
