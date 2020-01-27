package ru.filit.motiv.app.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.filit.motiv.app.presenters.interactors.SubscriberInteractor
import ru.filit.motiv.app.states.main.MyTariffPartialState
import ru.filit.motiv.app.states.main.MyTariffState
import ru.filit.motiv.app.views.main.MyTariffView

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