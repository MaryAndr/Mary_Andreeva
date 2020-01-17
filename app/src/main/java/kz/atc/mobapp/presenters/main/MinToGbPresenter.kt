package kz.atc.mobapp.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.states.main.MinToGbState
import kz.atc.mobapp.views.main.MinToGbView

class MinToGbPresenter (val ctx: Context) :
    MviBasePresenter<MinToGbView, MinToGbState>() {

    override fun bindIntents() { val fetchEnabledServicesIntent : Observable<MinToGbState> =
        intent(MinToGbView::changeQuantityIntent).flatMap {
            Observable.just(MinToGbState.EtQuantityChanged(it))
        }

        val allIntents = fetchEnabledServicesIntent
            .observeOn(AndroidSchedulers.mainThread())


        subscribeViewState(allIntents, MinToGbView::render)
    }

}