package kz.atc.mobapp.presenters

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.states.EnterSMSPagePartialState
import kz.atc.mobapp.states.EnterSMSPageState
import kz.atc.mobapp.views.EnterSMSPassView
import java.util.concurrent.TimeUnit

class EnterSMSPassPagePresenter(val ctx: Context) :
    MviBasePresenter<EnterSMSPassView, EnterSMSPageState>() {

    override fun bindIntents() {

        var resendIntent: Observable<EnterSMSPagePartialState> =
            intent(EnterSMSPassView::resendSMSIntent)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    resendTimer()
                }
                .observeOn(AndroidSchedulers.mainThread())

        val initialState = EnterSMSPageState(false, false, false, false, null, null)
        val stateObservable = resendIntent.scan(initialState, this::viewStateReducer)

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
            is EnterSMSPagePartialState.ShowResendTv -> {
                previousState.showResendText = true
                return previousState
            }
        }
    }

}