package com.kevinmost.lifx;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public abstract class BaseLifxTest {
  @NotNull final Logger logger = LoggerFactory.getLogger(getClass());

  @Before
  public void setupLifxClient() {
    new LifxClient.Builder(EnvVar.LIFX_ACCESS_TOKEN.value())
        .client(new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
              @Override public void log(String message) {
                logger.warn(message);
              }
            }).setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
        )
        .buildAsDefault();
  }

}
