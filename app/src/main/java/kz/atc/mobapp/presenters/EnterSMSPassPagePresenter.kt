package kz.atc.mobapp.presenters

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.models.ErrorJson
import kz.atc.mobapp.presenters.interactors.UserInteractor
import kz.atc.mobapp.states.EnterSMSPagePartialState
import kz.atc.mobapp.states.EnterSMSPageState
import kz.atc.mobapp.utils.TextConverter
import kz.atc.mobapp.views.EnterSMSPassView
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

class EnterSMSPassPagePresenter(val ctx: Context) :
    MviBasePresenter<EnterSMSPassView, EnterSMSPageState>() {
    private val gson = Gson()


    override fun bindIntents() {

        var authorizationAuth: Observable<EnterSMSPagePartialState> =
            intent(EnterSMSPassView::authorizeIntent)
                .flatMap { authData ->
                    UserInteractor().smsAuthorization(authData, ctx)
                        .onErrorResumeNext { error: Throwable ->
                            var errMessage = error.localizedMessage
                            if (error is HttpException) {
                                errMessage = if (error.code() == 409) {
                                    "Вы не являетесь пользователем мобильной связи"
                                } else {
                                    val errorBody = error.response()!!.errorBody()

                                    val adapter =
                                        gson.getAdapter<ErrorJson>(ErrorJson::class.java!!)
                                    val errorObj = adapter.fromJson(errorBody!!.string())
                                    errorObj.error_description
                                }
                            }
                            Observable.just(EnterSMSPagePartialState.ErrorState(errMessage))
                        }
                }


        var resendApiIntent: Observable<EnterSMSPagePartialState> =
            intent(EnterSMSPassView::resendSMSIntent)
                .flatMap {
                    UserInteractor().userService.sendSMS(
                        RequestBody.create(
                            MediaType.parse("text/plain"),
                            TextConverter().getOnlyDigits(it)
                        )
                    ).flatMap {
                        resendTimer()
                    }.subscribeOn(Schedulers.io())
                }

        var firstAttemptIntent: Observable<EnterSMSPagePartialState> =
            intent(EnterSMSPassView::firstAttemptIntent)
                .flatMap { resendTimer() }


        val initialState = EnterSMSPageState(
            false,
            showError = false,
            smsResended = false,
            autorize = false,
            errorMessage = null,
            countdown = null
        )
        val allIntents = Observable.merge(resendApiIntent, authorizationAuth, firstAttemptIntent)
            .observeOn(AndroidSchedulers.mainThread())

        val stateObservable = allIntents.scan(initialState, this::viewStateReducer)

        subscribeViewState(stateObservable, EnterSMSPassView::render)

    }

    private fun resendTimer(): Observable<EnterSMSPagePartialState> {
        val start: Long = 45
        return Observable.interval(0, 1, TimeUnit.SECONDS)
            .map { i -> start - i }
            .take(start + 1)
            .flatMap<EnterSMSPagePartialState> {
                Observable.just(EnterSMSPagePartialState.ShowTimerState(it))
            }.subscribeOn(Schedulers.io())
    }

    private fun viewStateReducer(
        previousState: EnterSMSPageState,
        changes: EnterSMSPagePartialState
    ): EnterSMSPageState {

        when (changes) {
            is EnterSMSPagePartialState.ShowTimerState -> {
                previousState.showTimer = true
                previousState.countdown = changes.countDown
                previousState.showError = false
                previousState.autorize = false
                return previousState
            }
            is EnterSMSPagePartialState.ErrorState -> {
                previousState.errorMessage = changes.error
                previousState.showError = true
                previousState.autorize = false
                return previousState
            }
            is EnterSMSPagePartialState.Authorized -> {
                Log.d("state", "Auth")
                previousState.autorize = true
                previousState.errorMessage = null
                previousState.showError = false
                return previousState
            }
            is EnterSMSPagePartialState.SmsResendedState -> {
                previousState.smsResended = true
                previousState.showError = false
                previousState.autorize = false
                return previousState
            }
            is EnterSMSPagePartialState.BlankState -> {
                return previousState
            }
        }
    }

}