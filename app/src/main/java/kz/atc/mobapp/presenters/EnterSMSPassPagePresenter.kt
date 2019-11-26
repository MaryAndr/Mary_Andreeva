package kz.atc.mobapp.presenters

import android.content.Context
import android.util.Log
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.presenters.interactors.UserInteractor
import kz.atc.mobapp.states.EnterSMSPagePartialState
import kz.atc.mobapp.states.EnterSMSPageState
import kz.atc.mobapp.views.EnterSMSPassView
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit

class EnterSMSPassPagePresenter(val ctx: Context) :
    MviBasePresenter<EnterSMSPassView, EnterSMSPageState>() {

    override fun bindIntents() {

        var resendIntent: Observable<EnterSMSPagePartialState> =
            intent(EnterSMSPassView::resendSMSIntent)
                .startWith{
                    UserInteractor().userService.sendSMS(
                        RequestBody.create(
                            MediaType.parse("text/plain"),
                            "9024900998"
                        )
                    ).flatMap {
                        Observable.just(EnterSMSPagePartialState.SmsResendedState)
                    }.subscribeOn(Schedulers.io())
                }
                .flatMap {
                    Log.d("INFO", "Resend Triggered")
                    resendTimer()
                }
                .subscribeOn(Schedulers.io())

        var resendApiIntent: Observable<EnterSMSPagePartialState> =
            intent(EnterSMSPassView::resendSMSIntent)
                .flatMap {
                    UserInteractor().userService.sendSMS(
                        RequestBody.create(
                            MediaType.parse("text/plain"),
                            it
                        )
                    ).flatMap {
                        resendTimer()
                    }.subscribeOn(Schedulers.io())
                }
        val initialState = EnterSMSPageState(
            false,
            showError = false,
            smsResended = false,
            autorize = false,
            errorMessage = null,
            countdown = null
        )
        val allIntents = resendApiIntent
            .observeOn(AndroidSchedulers.mainThread())

        val stateObservable = allIntents.scan(initialState, this::viewStateReducer)

        subscribeViewState(stateObservable, EnterSMSPassView::render)

    }

    private fun resendTimer(): Observable<EnterSMSPagePartialState> {
        val start: Long = 30
        return Observable.interval(1, TimeUnit.SECONDS)
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
                return previousState
            }
            is EnterSMSPagePartialState.ErrorState -> {
                previousState.errorMessage = changes.error
                previousState.showError = true
                return previousState
            }
            is EnterSMSPagePartialState.Authorized -> {
                previousState.autorize = true
                previousState.errorMessage = null
                return previousState
            }
            is EnterSMSPagePartialState.SmsResendedState -> {
                previousState.smsResended = true
                return previousState
            }
        }
    }

}