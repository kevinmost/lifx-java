package com.kevinmost.lifx.kt

import com.kevinmost.lifx.EnvVar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Before
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

abstract class BaseLifxTest {
  val logger: Logger = LoggerFactory.getLogger(javaClass);

  @Before fun setupLifxClient() {
    lifxClient(
        EnvVar.LIFX_ACCESS_TOKEN.value()!!,
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor { message -> logger.warn(message) })
            .build()
    ).buildAsDefault()
  }
}