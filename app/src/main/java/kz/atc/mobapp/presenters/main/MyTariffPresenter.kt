package kz.atc.mobapp.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.presenters.interactors.SubscriberInteractor
import kz.atc.mobapp.states.main.MyTariffPartialState
import kz.atc.mobapp.states.main.MyTariffState
import kz.atc.mobapp.views.main.MyTariffView

class MyTariffPresenter(val ctx: Context) :
    MviBasePresenter<MyTariffView, MyTariffState>() {

    private val subService = SubscriberInteractor(ctx)

    override fun bindIntents() {

        val mainDataLoadIntent: Observable<MyTariffPartialState> =
            intent(MyTariffView::preLoadIntent)
                .flatMap {
                    subService.getMyTariffMainData().subscribeOn(Schedulers.io())
                }.startWith(
                    MyTariffPartialState.Loading
                )

        val initialState = MyTariffState(false, null, false, null, false)


        val allIntents = mainDataLoadIntent
            .observeOn(AndroidSchedulers.mainThread())

        val stateObservable = allIntents.scan(initialState, this::viewStateReducer)

        subscribeViewState(stateObservable, MyTariffView::render)
    }

    private fun viewStateReducer(
        previousState: MyTariffState,
        changes: MyTariffPartialState
    ): MyTariffState {

        return when (changes) {
            is MyTariffPartialState.MainDataLoadedState -> {
                previousState.mainDataLoaded = true
                previousState.mainData = changes.data
                previousState
            }
            is MyTariffPartialState.ShowErrorMessage -> {
                previousState.errorShown = true
                previousState.errorText = changes.error
                previousState.mainDataLoaded = false
                previousState.mainData = null
                previousState
            }
            is MyTariffPartialState.Loading -> {
                previousState.loading = true
                previousState.mainDataLoaded = false
                previousState.errorShown = false
                previousState
            }
        }
    }

}