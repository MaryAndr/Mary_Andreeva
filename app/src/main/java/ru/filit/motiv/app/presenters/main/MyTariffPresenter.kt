package ru.filit.motiv.app.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.filit.motiv.app.models.main.ToggleButtonState
import ru.filit.motiv.app.presenters.interactors.SubscriberInteractor
import ru.filit.motiv.app.states.main.MyTariffPartialState
import ru.filit.motiv.app.states.main.MyTariffState
import ru.filit.motiv.app.views.main.MyTariffView
import java.util.concurrent.TimeUnit

class MyTariffPresenter(val ctx: Context) :
    MviBasePresenter<MyTariffView, MyTariffState>() {

    private val subService = SubscriberInteractor(ctx)

    override fun bindIntents() {

        val mainDataLoadIntent: Observable<MyTariffPartialState> =
            intent(MyTariffView::preLoadIntent)
                .flatMap {
                    subService.getMyTariffMainData().startWith(
                        MyTariffPartialState.Loading
                    ).subscribeOn(Schedulers.io())
                }

        val changeServiceIntent: Observable<MyTariffPartialState> =
            intent (MyTariffView::changeServiceIntent)
                .flatMap {
                    Observable.just(MyTariffPartialState.ChangeAvailableService(it) as MyTariffPartialState)
                        .delay (5000, TimeUnit.MILLISECONDS)
                        .startWith(MyTariffPartialState.Loading)
                }

        val cancelChange: Observable<MyTariffPartialState> =
            intent (MyTariffView::cancelChangeServiceIntent)
                .flatMap {model-> Observable.just(MyTariffPartialState.CancelChange(model) as MyTariffPartialState)}

        val changeInternetConnectivity: Observable<MyTariffPartialState> =
            intent ( MyTariffView::checkInternetConnectivityIntent )
                .flatMap {Observable.just(MyTariffPartialState.InternetState(it))}

        val initialState = MyTariffState(false, null, false, null, false, false,null, connectionLost = false, connectionResume = false)


        val allIntents = Observable.merge(
            mainDataLoadIntent
            ,cancelChange
            ,changeServiceIntent,
            changeInternetConnectivity)
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
                previousState.changeService = false
                previousState.mainDataLoaded = true
                previousState.mainData = changes.data
                previousState.loading = false
                previousState.changeServiceMessage = null
                previousState
            }
            is MyTariffPartialState.ShowErrorMessage -> {
                previousState.changeService = false
                previousState.errorShown = true
                previousState.errorText = changes.error
                previousState.mainDataLoaded = false
                previousState.mainData = null
                previousState.changeServiceMessage = null
                previousState
            }
            is MyTariffPartialState.Loading -> {
                previousState.changeService = false
                previousState.loading = true
                previousState.errorShown = false
                previousState.mainDataLoaded = false
                previousState.mainData = null
                previousState.changeServiceMessage = null
                previousState
            }
            is MyTariffPartialState.ChangeAvailableService -> {
                previousState.loading = false
                previousState.errorShown = false
                previousState.mainDataLoaded = false
                previousState.changeService = true
                previousState.mainData =null
                previousState.changeServiceMessage = changes.dialogMessage
                return previousState
            }
            is MyTariffPartialState.CancelChange -> {
                previousState.loading = false
                previousState.errorShown = false
                previousState.mainDataLoaded = true
                previousState.changeService = false
                previousState.changeServiceMessage = null
                previousState.mainData?.servicesList?.forEach{if (it.id==changes.idService)it.toggleState =ToggleButtonState.ActiveAndEnabled}
                return previousState
            }
            is MyTariffPartialState.InternetState -> {
                previousState.changeService = false
                previousState.loading = false
                previousState.errorShown = false
                previousState.mainDataLoaded = false
                previousState.mainData = null
                previousState.changeServiceMessage = null
                previousState.connectionLost = !changes.active
                previousState.connectionResume = changes.active
                return previousState
            }
        }
    }

}