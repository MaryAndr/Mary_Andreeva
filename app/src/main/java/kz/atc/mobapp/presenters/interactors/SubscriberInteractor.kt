package kz.atc.mobapp.presenters.interactors

import android.content.Context
import android.util.Log
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
        val accumData = MainPagaAccumData()
        return subService.getSubInfo().flatMap { subInfo ->
            accumData.phoneNumber = subInfo.msisdn
            subService.getSubTariff().flatMap { subTariff ->
                accumData.tariffName = subTariff.tariff.name
                accumData.chargeDate = subTariff.charge_date
                subService.getSubBalance().flatMap { subBalance ->
                    accumData.balance = subBalance.value
                    subService.getSubRemains().flatMap { subRemains ->
                        accumData.remains = subRemains
                        Observable.just(MainPagePartialState.ShowDataState(accumData))
                    }
                }
            }
        }
    }
}