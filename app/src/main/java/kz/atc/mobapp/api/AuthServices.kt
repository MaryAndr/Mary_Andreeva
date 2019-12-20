package kz.atc.mobapp.api

import io.reactivex.Observable
import kz.atc.mobapp.models.EmailCosts
import kz.atc.mobapp.models.OAuthModel
import kz.atc.mobapp.models.UserType
import kz.atc.mobapp.models.catalogTariff.CatalogServicesResponse
import kz.atc.mobapp.models.catalogTariff.CatalogTariffResponse
import kz.atc.mobapp.utils.AuthenticationInterceptor
import kz.atc.mobapp.utils.ContentTypeInterceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.*


interface AuthServices {


    @GET("/lk/v1/user/type")
    fun userTypeCheck(@Query("username") username: String): Observable<UserType>

    @Multipart
    @POST("/lk/v1/user/password")
    fun sendSMS(@Part("username") username: RequestBody?): Observable<Unit>

    @GET("/lk/v1/catalog/tariff")
    fun getCatalogTariff(@Query("trpl_id") id: Int): Observable<CatalogTariffResponse>


    @GET("/lk/v1/catalog/service")
    fun getCatalogService(@Query("serv_id") serviceId: String?, @Query("region") region: String?): Observable<CatalogServicesResponse>

    @Multipart
    @POST("/oauth/token")
    fun auth(
        @Part("grant_type") grantType: RequestBody, @Part("username") username: RequestBody?,
        @Part("password") password: RequestBody?, @Part("refresh_token") refreshToken: RequestBody?
    ): Observable<OAuthModel>


//    @POST("/oauth/token")
//    fun auth(@FieldMap requestBody: HashMap<String,String>
//    ) : Observable<OAuthModel>


    companion object {
        fun create(): AuthServices {
            val logging = HttpLoggingInterceptor()

            logging.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient().newBuilder()
                .addInterceptor(logging)
                .addInterceptor(AuthenticationInterceptor("testClient", "pass"))
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

            return retrofit.create(AuthServices::class.java)
        }
    }
}