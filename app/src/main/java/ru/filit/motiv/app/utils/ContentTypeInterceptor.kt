package ru.filit.motiv.app.utils

import okhttp3.Interceptor
import okhttp3.Response

class ContentTypeInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.header("Content-Type", "application/json;charset=utf-8")
        return chain.proceed(requestBuilder.build())
    }

}