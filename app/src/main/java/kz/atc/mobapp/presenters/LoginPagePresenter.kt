package kz.atc.mobapp.presenters

import android.content.Context
import android.util.Log
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.presenters.interactors.UserInteractor
import kz.atc.mobapp.states.LoginPagePartialState
import kz.atc.mobapp.states.LoginPageState
import kz.atc.mobapp.utils.HttpErrorHandler
import kz.atc.mobapp.views.LoginPageView
import retrofit2.HttpException

class LoginPagePresenter(val ctx: Context) : MviBasePresenter<LoginPageView, LoginPageState>() {


    override fun bindIntents() {
        val authorizeIntent: Observable<LoginPagePartialState> =
            intent(LoginPageView::authorizeIntent)
                .flatMap { authData ->
                    UserInteractor().completeAuthorization(authData)
                        .onErrorResumeNext { error: Throwable ->
                            var errMessage = error.localizedMessage
                            if (error is HttpException) {
                                if (error.code() == 409) {
                                    errMessage = "Вы не являетесь пользователем мобильной связи"
                                }
                            }
                            Observable.just(LoginPagePartialState.ErrorState(errMessage))
                        }
                }
                .retry()
                .subscribeOn(Schedulers.io())

        val reenterIntent: Observable<LoginPagePartialState> =
            intent(LoginPageView::reenterIntent)
                .map<LoginPagePartialState> { LoginPagePartialState.DefaultState }
                .subscribeOn(Schedulers.io())

        val initialState = LoginPageState(
            false,
            errorStateShown = false,
            errorMessage = null,
            successFullyAuthorized = false,
            defaultState = true
        )
        val allIntents: Observable<LoginPagePartialState> =
            Observable.merge(authorizeIntent, reenterIntent).observeOn(
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
                previousState.successFullyAuthorized = false
                previousState.defaultState = false
                return previousState
            }
            is LoginPagePartialState.Authorized -> {
                previousState.successFullyAuthorized = true
                previousState.errorMessage = null
                previousState.defaultState = false
                return previousState
            }
            is LoginPagePartialState.Loading -> {
                previousState.loading = true
                previousState.errorMessage = null
                previousState.defaultState = false
                return previousState
            }
        }
    }

}