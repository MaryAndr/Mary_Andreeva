package ru.filit.motiv.app.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.filit.motiv.app.presenters.interactors.SubscriberInteractor
import ru.filit.motiv.app.states.main.ServicesPartialState
import ru.filit.motiv.app.utils.isConnect
import ru.filit.motiv.app.views.main.ServicesPageView
import java.util.concurrent.TimeUnit

class   ServicesPresenter (val ctx: Context) : MviBasePresenter<ServicesPageView, ServicesPartialState>(){

    private val subService = SubscriberInteractor(ctx)

    override fun bindIntents() {
        val fetchEnabledServicesIntent : Observable<ServicesPartialState> =
            intent(ServicesPageView::showEnabledServiceIntent)
                .switchMap { isExistOnSub ->
                    if (!isConnect(ctx = ctx)){
                        return@switchMap Observable.just(ServicesPartialState.InternetState(false))
                    }
                subService.getEnabledServices(isExistOnSub).subscribeOn(Schedulers.io())
                    .startWith(
                        ServicesPartialState.Loading
                    )
            }

        val changeServiceIntent: Observable<ServicesPartialState> =
            intent (ServicesPageView::changeServiceIntent)
                .flatMap {
                    Observable.just(ServicesPartialState.ChangeAvailableService(it) as ServicesPartialState)
                        .delay (5000, TimeUnit.MILLISECONDS)
                        .startWith(ServicesPartialState.LoadingChangeService)
                }

        val cancelChange: Observable<ServicesPartialState> =
            intent (ServicesPageView::cancelChangeServiceIntent)
                .flatMap {model-> Observable.just(ServicesPartialState.CancelChange(model) as ServicesPartialState)}

        val changeInternetConnectionIntent: Observable<ServicesPartialState> =
            intent (ServicesPageView::checkInternetConnectivityIntent).flatMap {

                Observable.just(ServicesPartialState.InternetState(it))
            }


        val allIntents:Observable<ServicesPartialState> =
           Observable.merge(
               fetchEnabledServicesIntent
               ,changeServiceIntent
               ,cancelChange
               ,changeInternetConnectionIntent)
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(allIntents, ServicesPageView::render)
    }

/*    private fun viewStateReducer (previousState:ServicesState, changes:ServicesPartialState):ServicesState {

        when(changes) {
            is ServicesPartialState.FetchEnabledService -> {
                previousState.loadedAllServices = false
                previousState.loadedenabledServices = true
                previousState.services = changes.servicesList
                return previousState
            }
            is ServicesPartialState.ChangeAvailableService -> {
                var service: ServicesListShow =ServicesListShow()
                previousState.services?.forEach {
                    if (it.id==changes.id) {
                        if (previousState.loadedenabledServices){
                            service = it
                        }
                        if (changes.isActive){
                            it.toggleState = ToggleButtonState.ActiveAndEnabled
                        }else it.toggleState = ToggleButtonState.InactiveAndEnabled
                    }
                }
                previousState.services?.remove(service)
                return previousState
            }
            is ServicesPartialState.Loading -> {
                previousState.loading = true
                previousState.loadedAllServices = false
                previousState.loadedenabledServices = false
                return previousState
            }
            is ServicesPartialState.FetchAllService -> {
                previousState.loadedenabledServices = false
                previousState.loadedAllServices = true
                previousState.services = changes.servicesList
                return previousState
            }
        }
    }*/
}