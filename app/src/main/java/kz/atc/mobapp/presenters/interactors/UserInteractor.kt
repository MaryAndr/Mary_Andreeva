package kz.atc.mobapp.presenters.interactors

import android.content.Context
import android.util.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.api.AuthServices
import kz.atc.mobapp.models.AuthModel
import kz.atc.mobapp.states.EnterSMSPagePartialState
import kz.atc.mobapp.utils.PreferenceHelper.set
import kz.atc.mobapp.utils.PreferenceHelper.get
import kz.atc.mobapp.states.LoginPagePartialState
import kz.atc.mobapp.utils.Constants
import kz.atc.mobapp.utils.PreferenceHelper
import okhttp3.MediaType
import okhttp3.RequestBody

class UserInteractor {

    val userService by lazy {
        AuthServices.create()
    }

    fun completeAuthorization(
        authData: AuthModel,
        ctx: Context
    ): Observable<LoginPagePartialState> {
        return when {
            authData.username.length < 10 -> Observable.just(LoginPagePartialState.ErrorState("Введите корректный номер телефона"))
            authData.password.length < 8 -> Observable.just(LoginPagePartialState.ErrorState("Введите пароль или получите новый по SMS"))
            authData.password.isBlank() -> Observable.just(LoginPagePartialState.ErrorState("Пароль не может быть пустым"))
            authData.username.isBlank() -> Observable.just(LoginPagePartialState.ErrorState("Номер не может быть пустым"))
            else -> userService.userTypeCheck(authData.username).flatMap { result ->
                if (result.name != "Пользователь мобильной связи") {
                    Observable.just(LoginPagePartialState.ErrorState("Вы не являетесь пользователем мобильной связи"))
                } else {
                    userService.auth(
                        RequestBody.create(MediaType.parse("text/plain"), "password"),
                        RequestBody.create(MediaType.parse("text/plain"), authData.username)
                        ,
                        RequestBody.create(MediaType.parse("text/plain"), authData.password),
                        null
                    ).flatMap { oAuthResult ->
                        if (!oAuthResult.access_token.isBlank() && !oAuthResult.refresh_token.isBlank() && !oAuthResult.jti.isBlank()) {
                            val prefs = PreferenceHelper.customPrefs(ctx, Constants.AUTH_PREF_NAME)
                            prefs[Constants.AUTH_TOKEN] = oAuthResult.access_token
                            prefs[Constants.AUTH_REFRESH_TOKEN] = oAuthResult.refresh_token
                            Observable.just(LoginPagePartialState.Authorized)
                        } else {
                            Observable.just(LoginPagePartialState.ErrorState("Что-то пошло не так"))
                        }
                    }
                }
            }
        }
    }

    fun smsAuthorization(authData: AuthModel, ctx: Context): Observable<EnterSMSPagePartialState> {
        return when {
            authData.password.isBlank() -> Observable.just(EnterSMSPagePartialState.ErrorState("Пароль не может быть пустым"))
            else -> userService.auth(
                RequestBody.create(MediaType.parse("text/plain"), "password"),
                RequestBody.create(MediaType.parse("text/plain"), authData.username),
                RequestBody.create(MediaType.parse("text/plain"), authData.password),
                null
            ).flatMap { oAuthResult ->
                if (!oAuthResult.access_token.isBlank() && !oAuthResult.refresh_token.isBlank() && !oAuthResult.jti.isBlank()) {
                    val prefs = PreferenceHelper.customPrefs(ctx, Constants.AUTH_PREF_NAME)
                    prefs[Constants.AUTH_TOKEN] = oAuthResult.access_token
                    prefs[Constants.AUTH_REFRESH_TOKEN] = oAuthResult.refresh_token
                    Observable.just(EnterSMSPagePartialState.Authorized)
                } else {
                    Observable.just(EnterSMSPagePartialState.ErrorState("Что-то пошло не так"))
                }
            }.subscribeOn(Schedulers.io())

        }
    }


    fun isAuthenticated(ctx: Context): Observable<LoginPagePartialState> {
        val prefs = PreferenceHelper.customPrefs(ctx, Constants.AUTH_PREF_NAME)
        val authToken: String? = prefs[Constants.AUTH_TOKEN]

        return when {
            !authToken.isNullOrEmpty() -> Observable.just(LoginPagePartialState.Authorized)
            else -> {
                Observable.just(LoginPagePartialState.DefaultState)
            }
        }
    }

}