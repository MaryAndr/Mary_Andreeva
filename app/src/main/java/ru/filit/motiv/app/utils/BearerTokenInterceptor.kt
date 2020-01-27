package ru.filit.motiv.app.utils

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class BearerTokenInterceptor(bearerToken: String) : Interceptor {

    private val bToken = bearerToken

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()
            .header("Authorization", "Bearer $bToken").build()
        return chain.proceed(authenticatedRequest)
    }

}