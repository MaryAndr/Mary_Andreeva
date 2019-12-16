package kz.atc.mobapp.api

import android.content.Context
import io.reactivex.Observable
import kz.atc.mobapp.models.*
import kz.atc.mobapp.models.main.SubPaymentsResponse
import kz.atc.mobapp.oauth.TokenAuthenticator
import kz.atc.mobapp.utils.*
import kz.atc.mobapp.utils.PreferenceHelper.get
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface SubscriberServices {

    @GET("/lk/v1/subscriber/info")
    fun getSubInfo(): Observable<SubInfoResponse>

    @GET("/lk/v1/subscriber/tariff")
    fun getSubTariff(): Observable<TariffResponse>

    @GET("/lk/v1/subscriber/balance")
    fun getSubBalance(): Observable<SubBalanceResponse>

    @GET("/lk/v1/subscriber/remains")
    fun getSubRemains(): Observable<List<RemainsResponse>>

    @GET("/lk/v1/subscriber/exchange")
    fun getExchangeInfo() : Observable<ExchangeResponse>

    @POST("/lk/v1/subscriber/details")
    fun sendDetalization(@Body emailDetal: EmailCosts) : Observable<EmailCosts>

    @GET("/lk/v1/subscriber/payments")
    fun getSubPayments(@Query("date_from") dateFrom: String?, @Query("date_to") dateTo: String?) : Observable<List<SubPaymentsResponse>>

    companion object {
        fun create(ctx: Context): SubscriberServices {
            val logging = HttpLoggingInterceptor()
            val prefs = PreferenceHelper.customPrefs(ctx, Constants.AUTH_PREF_NAME)
            val authToken: String? = prefs[Constants.AUTH_TOKEN]

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