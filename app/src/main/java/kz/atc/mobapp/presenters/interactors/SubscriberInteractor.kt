package kz.atc.mobapp.presenters.interactors

import android.content.Context
import android.util.Log
import android.view.View
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.api.SubscriberServices
import kz.atc.mobapp.models.CatalogTariffResponse
import kz.atc.mobapp.models.RemainsResponse
import kz.atc.mobapp.models.TariffResponse
import kz.atc.mobapp.models.main.IndicatorHolder
import kz.atc.mobapp.models.main.MainPagaAccumData
import kz.atc.mobapp.states.main.MainPagePartialState
import kz.atc.mobapp.utils.MathUtils
import kz.atc.mobapp.utils.StringUtils

class SubscriberInteractor(ctx: Context) {


    private val subService by lazy {
        SubscriberServices.create(ctx)
    }


    fun preLoadData(): Observable<MainPagePartialState> {
        val accumData = MainPagaAccumData()
        return subService.getSubInfo().flatMap { subInfo ->
            accumData.phoneNumber = subInfo.msisdn
            subService.getSubTariff().flatMap { subTariff ->
                accumData.tariffData = subTariff
                accumData.chargeDate = subTariff.charge_date
                subService.getSubBalance().flatMap { subBalance ->
                    accumData.balance = subBalance.value
                    subService.getSubRemains().flatMap { subRemains ->
                        accumData.remains = subRemains
                        subService.getCatalogTariff(subTariff.tariff.id).flatMap { catalogTariff ->
                            accumData.indicatorHolder =
                                calculateIndicators(subTariff, subRemains, catalogTariff)
                            Observable.just(MainPagePartialState.ShowDataState(accumData))
                        }
                    }
                }
            }
        }
    }

    private fun calculateIndicators(
        tariff: TariffResponse,
        remains: List<RemainsResponse>,
        catalogTariff: CatalogTariffResponse
    ): MutableMap<String, IndicatorHolder> {
        val outputMap: MutableMap<String, IndicatorHolder> = mutableMapOf()
        remains.filter{predicate -> predicate.services.primary}.forEach {
            if (it.type == "DATA") {
                var rest = it.rest_amount
                var total = it.total_amount
                val indicatorData =
                    IndicatorHolder(rest, total, MathUtils().calculatePercent(rest, total), false)
                outputMap?.put("DATA", indicatorData)
            }
            if (it.type == "VOICE") {
                var rest = it.rest_amount
                var total = it.total_amount
                val indicatorData =
                    IndicatorHolder(rest, total, MathUtils().calculatePercent(rest, total), false)
                outputMap?.put("VOICE", indicatorData)
            }
            if (it.type == "SMS") {
                var rest = it.rest_amount
                var total = it.total_amount
                val indicatorData =
                    IndicatorHolder(rest, total, MathUtils().calculatePercent(rest, total), false)
                outputMap?.put("SMS", indicatorData)
            }
        }
        tariff.options.filter { predicate -> predicate.primary }.forEach {
            val indicatorHolder = IndicatorHolder(null,null, null, true)
            if (it.type == "DATA" && !outputMap.containsKey("DATA")) {
                outputMap?.put("DATA",indicatorHolder)
            }
            if (it.type == "VOICE" && !outputMap.containsKey("VOICE")) {
                outputMap?.put("VOICE",indicatorHolder)
            }
            if (it.type == "SMS" && !outputMap.containsKey("SMS")) {
                outputMap?.put("SMS",indicatorHolder)
            }
        }
        catalogTariff.attributes.forEach {
            if (it.system_name == "internet_gb_count" && !outputMap.containsKey("DATA")) {
                val indicatorHolder = IndicatorHolder(null,null,null,false,it.value + it.unit)
                outputMap?.put("DATA", indicatorHolder)
            }
            if (it.system_name == "minute_cost" && !outputMap.containsKey("VOICE")) {
                val indicatorHolder = IndicatorHolder(null,null,null,false,it.value + it.unit)
                outputMap?.put("VOICE", indicatorHolder)
            }
            if (it.system_name == "sms_count" && !outputMap.containsKey("SMS")) {
                val indicatorHolder = IndicatorHolder(null,null,null,false,it.value + it.unit)
                outputMap?.put("SMS", indicatorHolder)
            }
        }
        return outputMap
    }
}