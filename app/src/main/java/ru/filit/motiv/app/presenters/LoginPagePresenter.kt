package ru.filit.motiv.app.presenters

import android.content.Context
import com.google.gson.Gson
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.filit.motiv.app.models.ErrorJson
import ru.filit.motiv.app.presenters.interactors.UserInteractor
import ru.filit.motiv.app.states.LoginPagePartialState
import ru.filit.motiv.app.states.LoginPageState
import ru.filit.motiv.app.views.LoginPageView
import retrofit2.HttpException


class LoginPagePresenter(val ctx: Context) : MviBasePresenter<LoginPageView, LoginPageState>() {

    private val gson = Gson()

    override fun bindIntents() {
        val authorizeIntent: Observable<LoginPagePartialState> =
            intent(LoginPageView::authorizeIntent)
                .flatMap { authData ->
                    UserInteractor(ctx = ctx).completeAuthorization(authData)
                        .startWith(LoginPagePartialState.Loading)
                        .onErrorResumeNext { error: Throwable ->
                            var errMessage = "Произошла обшибка при попытке подключения. Повторите попытку позже."
                            if (error is HttpException) {

                                errMessage = if (error.code() == 409) {
                                    "Вы не являетесь пользователем мобильной связи Мотив"
                                } else {
                                    val errorBody = error.response()!!.errorBody()

                                    val adapter =
                                        gson.getAdapter<ErrorJson>(ErrorJson::class.java!!)
                                    val errorObj = adapter.fromJson(errorBody!!.string())
                                    if (errorObj.error_description == "Некорректный запрос") {
                                        "Неверный пароль"
                                    } else {
                                        errorObj.error_description
                                    }
                                }
                            }
                            Observable.just(LoginPagePartialState.ErrorState(errMessage))
                        }.subscribeOn(Schedulers.io())
                }

        val checkAuthIntent: Observable<LoginPagePartialState> =
            intent(LoginPageView::checkAuthIntent)
                .flatMap {
                    UserInteractor(ctx = ctx).isAuthenticated()
                }
                .subscribeOn(Schedulers.io())

        val reenterIntent: Observable<LoginPagePartialState> =
            intent(LoginPageView::reenterIntent)
                .map<LoginPagePartialState> {
                    LoginPagePartialState.DefaultState
                }
                .subscribeOn(Schedulers.io())

        val initialState = LoginPageState(
            false,
            errorStateShown = false,
            errorMessage = null,
            successFullyAuthorized = false,
            defaultState = true
        )
        val allIntents: Observable<LoginPagePartialState> =
            Observable.merge(authorizeIntent, reenterIntent, checkAuthIntent).observeOn(
                AndroidSchedulers.mainThread()
            )

        val stateObservable = allIntents.scan(initialState, this::viewStateReducer)

        subscribeViewState(stateObservable, LoginPageView::render)

    }


    private fun viewStateReducer(
        previousState: LoginPageState,
        changes: LoginPagePartialState
    ): LoginPageState {

        when (changes) {
            is LoginPagePartialState.DefaultState -> {
                previousState.successFullyAuthorized = false
                previousState.errorMessage = null
                previousState.loading = false
                previousState.errorStateShown = false
                previousState.defaultState = true
                return previousState
            }
            is LoginPagePartialState.ErrorState -> {
                previousState.errorMessage = changes.error
                previousState.errorStateShown = true
                previousState.loading = false
                previousState.successFullyAuthorized = false
                previousState.defaultState = false
                return previousState
            }
            is LoginPagePartialState.Authorized -> {
                previousState.loading = false
                previousState.successFullyAuthorized = true
                previousState.errorMessage = null
                previousState.defaultState = false
                return previousState
            }
            is LoginPagePartialState.Loading -> {
                previousState.loading = true
                previousState.errorMessage = null
                previousState.defaultState = false
                previousState.successFullyAuthorized = false
                return previousState
            }
        }
    }

}