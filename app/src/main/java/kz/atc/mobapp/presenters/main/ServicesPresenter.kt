package kz.atc.mobapp.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.presenters.interactors.SubscriberInteractor
import kz.atc.mobapp.states.main.ServicesState
import kz.atc.mobapp.views.main.ServicesPageView

class ServicesPresenter (val ctx: Context) : MviBasePresenter<ServicesPageView, ServicesState>(){

    private val subService = SubscriberInteractor(ctx)

    override fun bindIntents() {
        val fetchEnabledServicesIntent : Observable<ServicesState> =
            intent(ServicesPageView::showEnabledServiceIntent).flatMap { isExistOnSub ->
                subService.getEnabledServices(isExistOnSub)
                    .flatMap {
                        if (isExistOnSub) {
                            Observable.just(ServicesState.FetchEnabledService(it))
                        } else {
                            Observable.just(ServicesState.FetchAllService(it))
                        }
                    }.startWith(
                        ServicesState.Loading
                    )
                    .subscribeOn(Schedulers.io())
            }

        val allIntents = fetchEnabledServicesIntent
            .observeOn(AndroidSchedulers.mainThread())


        subscribeViewState(allIntents, ServicesPageView::render)
    }

}