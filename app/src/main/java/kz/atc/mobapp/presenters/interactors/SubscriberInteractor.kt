package kz.atc.mobapp.presenters.interactors

import android.content.Context
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.api.SubscriberServices
import kz.atc.mobapp.models.main.MainPagaAccumData
import kz.atc.mobapp.states.main.MainPagePartialState

class SubscriberInteractor(ctx: Context) {


    private val subService by lazy {
        SubscriberServices.create(ctx)
    }


    fun preLoadData(): Observable<MainPagePartialState> {

        return subService.getSubInfo().flatMap { subInfo ->
            Observable.just(MainPagePartialState.ShowDataState(MainPagaAccumData(subInfo.msisdn)))
        }
    }
}