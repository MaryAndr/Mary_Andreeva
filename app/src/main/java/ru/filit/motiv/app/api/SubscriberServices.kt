package ru.filit.motiv.app.api

import android.content.Context
import io.reactivex.Observable
import ru.filit.motiv.app.models.*
import ru.filit.motiv.app.models.main.*
import ru.filit.motiv.app.oauth.TokenAuthenticator
import ru.filit.motiv.app.utils.*
import ru.filit.motiv.app.utils.PreferenceHelper.get
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface SubscriberServices {

    @GET("/lk/v1/subscriber/tariff/available")
    fun getAvailableTariffs(): Observable<List<AvailableTariffs>>

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

    @GET("/lk/v1/subscriber/transferred_history")
    fun getTransferedHistory() : Observable<List<TransferredHistoryResponse>>

    @GET("/lk/v1/subscriber/services")
    fun getServicesList() : Observable<List<ServicesListResponse>>

    @GET("/lk/v1/subscriber/services/available")
    fun getAllServicesList() : Observable<List<ServicesListResponse>>

    @DELETE("/lk/v1/subscriber/services/service/{serv_id}")
    fun deleteService(@Path("serv_id") servId: String?) : Observable<DeleteServiceResponse>

    @POST("/lk/v1/subscriber/services/service/{serv_id}")
    fun activateService(@Path("serv_id") servId: String?) : Observable<DeleteServiceResponse>

    @PUT("/lk/v1/subscriber/tariff")
    fun changeTariff(@Body id: TariffChangeRequest) : Observable<TariffChangeResponse>

    @POST("/lk/v1/subscriber/exchange")
    fun exchangeMins(@Body minutes: ExchangeRequest) : Observable<Unit>

    @GET("/lk/v1/subscriber/info")
    fun subscriberInfo() : Observable<SubscriberInfoResponse>

    @GET("/lk/v1/subscriber/status")
    fun subscriberStatus() : Observable<SubscriberStatusResponse>

    @PUT("/lk/v1/subscriber/status")
    fun changeStatus(@Body obj: BlockUnblockRequest) : Observable<BlockUnblockResponse>

    @PUT("/lk/v1/user/password")
    fun changePass(@Body obj: ChangePassRequest) : Observable<Unit>

    @POST("/lk/v1/user/logout")
    fun logout(): Observable<Unit>

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
                .addInterceptor(IdentificatorsInterceptor(ctx= ctx))
                .build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .client(client)
                .baseUrl("https://mptest.motivtelecom.ru")
                .build()

            return retrofit.create(SubscriberServices::class.java)
        }
    }
}