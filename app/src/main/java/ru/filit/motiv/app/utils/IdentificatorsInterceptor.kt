package ru.filit.motiv.app.utils

import android.content.Context
import android.os.Build
import okhttp3.Interceptor
import okhttp3.Response
import ru.filit.motiv.app.BuildConfig
import java.io.IOException

class IdentificatorsInterceptor (val ctx: Context):Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("version", BuildConfig.VERSION_NAME)
            .header("uuid", Installation.uuid(ctx))
            .header("platform", "Android")
            .header("os", Build.VERSION.SDK_INT.toString())
            .header("device", Build.MODEL).build()
       return chain.proceed(request)
    }
}