package kz.atc.mobapp.api

import android.content.Context
import android.util.Log
import io.reactivex.Observable
import kz.atc.mobapp.models.SubInfoResponse
import kz.atc.mobapp.oauth.TokenAuthenticator
import kz.atc.mobapp.utils.*
import kz.atc.mobapp.utils.PreferenceHelper.get
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SubscriberServices {

    @GET("/lk/v1/subscriber/info")
    fun getSubInfo(): Observable<SubInfoResponse>

    companion object {
        fun create(ctx: Context): SubscriberServices {
            val logging = HttpLoggingInterceptor()
            val prefs = PreferenceHelper.customPrefs(ctx, Constants.AUTH_PREF_NAME)
            val authToken: String? = prefs[Constants.AUTH_TOKEN]
            Log.d("TOKEN", authToken)

            logging.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient().newBuilder()
                .authenticator(TokenAuthenticator(ctx))
                .addInterceptor(logging)
                .addInterceptor(BearerTokenInterceptor(authToken!!))
                .addInterceptor(ContentTypeInterceptor())
                .build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .client(client)
                .baseUrl("http://10.241.12.201:30100")
                .build()

            return retrofit.create(SubscriberServices::class.java)
        }
    }
}