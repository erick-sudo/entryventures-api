package com.entryventures.apis.infobid

import okhttp3.Interceptor
import okhttp3.Response

class InfoBidRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val apiKey = "9b6967ae27f267b9680a6c796897b751-9ab464d7-c9af-4042-859f-0f420cdc2edb"

        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "App $apiKey")
            .addHeader("Accept", "application/json")
            .build()

        return chain.proceed(newRequest)
    }
}