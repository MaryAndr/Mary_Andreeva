package kz.atc.mobapp.presenters.interactors

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.api.AuthServices
import kz.atc.mobapp.models.AuthModel
import kz.atc.mobapp.models.OAuthModel
import kz.atc.mobapp.states.LoginPagePartialState
import okhttp3.MediaType
import okhttp3.RequestBody

class UserInteractor {

    val userService by lazy {
        AuthServices.create()
    }

    fun completeAuthorization(authData: AuthModel): Observable<LoginPagePartialState> {
        return when {
            authData.password.isBlank() -> Observable.just(LoginPagePartialState.ErrorState("Пароль не может быть пустым"))
            authData.username.isBlank() -> Observable.just(LoginPagePartialState.ErrorState("Номер не может быть пустым"))
            else -> userService.userTypeCheck(authData.username).flatMap { result ->
                if (result.name != "Пользователь мобильной связи") {
                    Observable.just(LoginPagePartialState.ErrorState("Вы не являетесь пользователем мобильной связи"))
                } else {
                    userService.auth(
                        RequestBody.create(MediaType.parse("text/plain"),"password"),RequestBody.create(MediaType.parse("text/plain"), authData.username)
                        , RequestBody.create(MediaType.parse("text/plain"), authData.password), null).flatMap { oAuthResult ->
                        if (!oAuthResult.access_token.isBlank() &&  !oAuthResult.refresh_token.isBlank() && !oAuthResult.jti.isBlank()) {
                            Observable.just(LoginPagePartialState.Authorized)
                        } else {
                            Observable.just(LoginPagePartialState.ErrorState("Что-то пошло не так"))
                        }
                    }
                }
            }.subscribeOn(Schedulers.io())
        }
    }

}