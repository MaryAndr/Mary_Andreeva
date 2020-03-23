package ru.filit.motiv.app.api

import android.content.Context
import io.reactivex.Observable
import ru.filit.motiv.app.models.OAuthModel
import ru.filit.motiv.app.models.UserType
import ru.filit.motiv.app.models.catalogTariff.CatalogServicesResponse
import ru.filit.motiv.app.models.catalogTariff.CatalogTariffResponse
import ru.filit.motiv.app.utils.AuthenticationInterceptor
import ru.filit.motiv.app.utils.ContentTypeInterceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.*
import ru.filit.motiv.app.models.main.FAQResponse
import ru.filit.motiv.app.utils.IdentificatorsInterceptor


interface AuthServices {


    @GET("/lk/v1/user/type")
    fun userTypeCheck(@Query("username") username: String): Observable<UserType>

    @Multipart
    @POST("/lk/v1/user/password")
    fun sendSMS(@Part("username") username: RequestBody?): Observable<Unit>

    @GET("/lk/v1/catalog/tariff")
    fun getCatalogTariff(@Query("trpl_id") id: String): Observable<CatalogTariffResponse>


    @GET("/lk/v1/catalog/service")
    fun getCatalogService(@Query("serv_id") serviceId: String?, @Query("region") region: String?): Observable<CatalogServicesResponse>

    @Multipart
    @POST("/oauth/token")
    fun auth(
        @Part("grant_type") grantType: RequestBody, @Part("username") username: RequestBody?,
        @Part("password") password: RequestBody?, @Part("refresh_token") refreshToken: RequestBody?
    ): Observable<OAuthModel>

    @GET("/lk/v1/catalog/faq")
    fun getFAQ(@Query ("region_id") regionId: Int? = null, @Query("question_id") questionId:Int? = null): Observable<List<FAQResponse>>


//    @POST("/oauth/token")
//    fun auth(@FieldMap requestBody: HashMap<String,String>
//    ) : Observable<OAuthModel>


    companion object {
        fun create(ctx: Context): AuthServices {
            val logging = HttpLoggingInterceptor()

            logging.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient().newBuilder()
                .addInterceptor(logging)
                .addInterceptor(AuthenticationInterceptor("testClient", "pass"))
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
               // .baseUrl("https://mptest.motivtelecom.ru")
                .build()

            return retrofit.create(AuthServices::class.java)
        }
    }
}