package com.kevinmost.lifx.kt

import com.kevinmost.lifx.LifxClient
import okhttp3.HttpUrl
import okhttp3.OkHttpClient

fun lifxClient(
    accessToken: String,
    client: OkHttpClient? = null,
    baseURL: HttpUrl? = null
): LifxClient.Builder = LifxClient.Builder(accessToken)
    .apply {
      client?.let { client(it) }
      baseURL?.let { baseURL(it) }
    }
