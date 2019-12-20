package kz.atc.mobapp.presenters.interactors

import android.content.Context
import android.util.Log
import android.view.View
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.reactivex.functions.Function4
import io.reactivex.functions.Function5
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.api.AuthServices
import kz.atc.mobapp.api.SubscriberServices
import kz.atc.mobapp.models.*
import kz.atc.mobapp.models.catalogTariff.CatalogTariffResponse
import kz.atc.mobapp.models.main.IndicatorHolder
import kz.atc.mobapp.models.main.MainPagaAccumData
import kz.atc.mobapp.models.main.MyTariffMainData
import kz.atc.mobapp.models.main.ServicesListShow
import kz.atc.mobapp.states.main.CostAndReplenishmentPartialState
import kz.atc.mobapp.states.main.CostsEmailState
import kz.atc.mobapp.states.main.MainPagePartialState
import kz.atc.mobapp.states.main.MyTariffPartialState
import kz.atc.mobapp.utils.MathUtils
import kz.atc.mobapp.utils.StringDateComparator
import kz.atc.mobapp.utils.StringUtils
import kz.atc.mobapp.utils.TimeUtils
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.*
import kotlin.Function3 as Function31

class SubscriberInteractor(ctx: Context) {


    private val subService by lazy {
        SubscriberServices.create(ctx)
    }

    private val userService by lazy {
        AuthServices.create()
    }

    fun getMyTariffMainData(): Observable<MyTariffPartialState> {
        val subTariff = subService.getSubTariff().onErrorReturn {
            null
        }

        val subInfo = subService.getSubInfo().onErrorReturn {
            null
        }

        val transHistory = subService.getTransferedHistory().onErrorReturn {
            mutableListOf()
        }

//        subTariff.flatMap { subTariffResponse ->
//            val mainData = MyTariffMainData(subTariffResponse)
//            userService.getCatalogTariff(subTariffResponse.tariff.id).flatMap{
//                mainData.catalogTariff = it
//                Observable.just(MyTariffPartialState.MainDataLoadedState(mainData))
//            }.onErrorReturn {
//                mainData.catalogTariff = null
//                MyTariffPartialState.MainDataLoadedState(mainData)
//            }
//
//        }


        return Observable.combineLatest(
            subTariff,
            subInfo,
            transHistory,
            Function3 { subTariffResponse, subInfoResponse, transHistory ->
                val mainData = MyTariffMainData(subTariffResponse)
                userService.getCatalogTariff(subTariffResponse.tariff.id).flatMap {
                    mainData.catalogTariff = it
                    Observable.just(mainData)
                }.onErrorReturn {
                    mainData.catalogTariff = null
                    mainData
                }.blockingFirst()

                subService.getServicesList().subscribe {
                    it.forEach { service ->
                        val serviceShow = ServicesListShow()
                        serviceShow.serviceName = service.name

                        userService.getCatalogService(
                             service.id.toString(),
                            subInfoResponse.region.name
                        ).flatMap { serviceDetails ->
                            serviceShow.description = serviceDetails.services.first()?.attributes.first{predicate -> predicate.system_name == "short_description"}?.value
                            Observable.just(serviceShow)
                        }.onErrorReturn {
                            serviceShow.description = null
                            serviceShow
                        }.blockingFirst()
                        mainData.servicesList.add(serviceShow)
                    }
                }



                MyTariffPartialState.MainDataLoadedState(mainData)
            })

    }

    fun getReplenishmentData(period: String): Observable<CostAndReplenishmentPartialState> {
        val dates = period.split("-")
        var dateFrom: String?
        var dateTo: String?
        if (dates.size > 1) {
            dateFrom = TimeUtils().changeFormat(
                dates[0],
                "dd.MM.yyyy",
                "yyyy-MM-dd"
            )
            dateTo = TimeUtils().changeFormat(dates[1], "dd.MM.yyyy", "yyyy-MM-dd")

        } else {
            dateFrom = TimeUtils().changeFormat(period, "dd.MM.yyyy", "yyyy-MM-dd")
            dateTo = TimeUtils().changeFormat(period, "dd.MM.yyyy", "yyyy-MM-dd")
        }

        return subService.getSubPayments(
            dateFrom
            , dateTo
        ).flatMap {
            var sortedList = it
            Collections.sort(sortedList, StringDateComparator())
            Observable.just(CostAndReplenishmentPartialState.ShowReplenishmentData(sortedList))
        }
    }

    fun sendDetalEmail(emailDetalModel: EmailDetalModel): Observable<CostsEmailState> {

        if (StringUtils().isEmailValid(emailDetalModel.email)) {

            val emailCosts = EmailCosts()
            val dates = emailDetalModel.period.split("-")
            if (dates.size > 1) {
                emailCosts.date_from =
                    TimeUtils().changeFormat(dates[0], "dd.MM.yyyy", "yyyy-MM-dd")
                emailCosts.date_to = TimeUtils().changeFormat(dates[1], "dd.MM.yyyy", "yyyy-MM-dd")
            } else {
                emailCosts.date_from =
                    TimeUtils().changeFormat(emailDetalModel.period, "dd.MM.yyyy", "yyyy-MM-dd")
                emailCosts.date_to =
                    TimeUtils().changeFormat(emailDetalModel.period, "dd.MM.yyyy", "yyyy-MM-dd")
            }
            emailCosts.emails!!.add(emailDetalModel.email)

            return subService.sendDetalization(emailCosts).flatMap {
                Observable.just(CostsEmailState.EmailSent)
            }
        } else {
            return Observable.just(CostsEmailState.ErrorShown("Некорректный email"))
        }
    }

    fun msisdnLoad(): Observable<CostsEmailState> {
        return subService.getSubInfo().flatMap {
            val monthAgo = Calendar.getInstance()
            monthAgo.add(Calendar.DAY_OF_MONTH, -30)
            val period =
                "${TimeUtils().dateToString(monthAgo)}-${TimeUtils().dateToString(Calendar.getInstance())}"
            Observable.just(CostsEmailState.MsisdnShown(it.msisdn, period))
        }
    }


    fun costsMainData(): Observable<CostAndReplenishmentPartialState> {
        val subInfo = subService.getSubInfo().onErrorReturn {
            null
        }

        val subBalance = subService.getSubBalance().onErrorReturn {
            null
        }

        val subTariff = subService.getSubTariff().onErrorReturn {
            null
        }
        return Observable.combineLatest(
            subInfo,
            subTariff,
            subBalance,
            Function3 { subInfo, subT, subBal ->
                val accumData = MainPagaAccumData()
                accumData.tariffData = subT
                accumData.phoneNumber = subInfo.msisdn
                accumData.balance = subBal.value
                CostAndReplenishmentPartialState.ShowMainDataState(accumData)
            })

    }


    fun preLoadData(): Observable<MainPagePartialState> {


        val subInfo = subService.getSubInfo().onErrorReturn {
            null
        }

        val subTariff = subService.getSubTariff().onErrorReturn {
            null
        }

        val subBalance = subService.getSubBalance().onErrorReturn {
            null
        }

        val subRemains = subService.getSubRemains().onErrorReturn {
            mutableListOf()
        }

        val exchangeInfo = subService.getExchangeInfo()


        return Observable.combineLatest(
            subInfo,
            subTariff,
            subBalance,
            subRemains,
            exchangeInfo,
            Function5 { subInfo, subT, subBal, subRem, subEx ->
                val accumData = MainPagaAccumData()
                accumData.subExchange = subEx
                accumData.phoneNumber = subInfo.msisdn
                accumData.tariffData = subT
                if (subT.charge_date != null) {
                    accumData.chargeDate = TimeUtils().debitDate(subT.charge_date)
                } else {
                    accumData.chargeDate = ""
                }
                accumData.balance = subBal.value
                accumData.remains = subRem
                if (subT.tariff != null) {
                    userService.getCatalogTariff(subT.tariff.id).flatMap { catTar ->
                        Log.d("DEBUG", "HERE")
                        accumData.indicatorHolder = calculateIndicators(subT, subRem, catTar)
                        Observable.just(MainPagePartialState.ShowDataState(accumData))
                    }.onErrorReturn {
                        accumData.indicatorHolder = calculateIndicators(subT, subRem, null)
                        MainPagePartialState.ShowDataState(accumData)
                    }.blockingFirst()
                } else {
                    accumData.indicatorHolder = calculateIndicators(subT, subRem, null)
                    MainPagePartialState.ShowDataState(accumData)
                }
            })

    }

    private fun calculateIndicators(
        tariff: TariffResponse,
        remains: List<RemainsResponse>?,
        catalogTariff: CatalogTariffResponse?
    ): MutableMap<String, IndicatorHolder> {
        val outputMap: MutableMap<String, IndicatorHolder> = mutableMapOf()
        remains?.filter { predicate -> predicate.services.primary }?.forEach {
            if (it.type == "DATA") {
                Log.d("HERE", "DATA")
                var rest = it.rest_amount
                var total = it.total_amount
                val indicatorData =
                    IndicatorHolder(rest, total, MathUtils().calculatePercent(rest, total), false)
                outputMap?.put("DATA", indicatorData)
            }
            if (it.type == "VOICE") {
                Log.d("HERE", "VOICES")
                var rest = it.rest_amount
                var total = it.total_amount
                val indicatorData =
                    IndicatorHolder(rest, total, MathUtils().calculatePercent(rest, total), false)
                outputMap?.put("VOICE", indicatorData)
            }
            if (it.type == "SMS") {
                Log.d("HERE", "SMS")
                var rest = it.rest_amount
                var total = it.total_amount
                val indicatorData =
                    IndicatorHolder(rest, total, MathUtils().calculatePercent(rest, total), false)
                outputMap?.put("SMS", indicatorData)
            }
        }
        tariff.options.filter { predicate -> predicate.primary }.forEach {

            if (it.type == "DATA" && !outputMap.containsKey("DATA")) {
                val indicatorHolder = IndicatorHolder(null, null, null, true, null, it.name)
                Log.d("HERE", "DATA1")
                outputMap?.put("DATA", indicatorHolder)
            }
            if (it.type == "VOICE" && !outputMap.containsKey("VOICE")) {
                val indicatorHolder = IndicatorHolder(null, null, null, true, null, it.name)
                Log.d("HERE", "VOICES1")
                outputMap?.put("VOICE", indicatorHolder)
            }
            if (it.type == "SMS" && !outputMap.containsKey("SMS")) {
                val indicatorHolder = IndicatorHolder(null, null, null, true, null, it.name)
                Log.d("HERE", "SMS1")
                outputMap?.put("SMS", indicatorHolder)
            }
        }

        catalogTariff?.tariffs?.first()?.attributes?.forEach {
            if (it.system_name == "internet_mb_cost" && !outputMap.containsKey("DATA")) {
                Log.d("HERE", "DATA2")
                val value = if (it.value.trim() == "Безлимит") {
                    it.value
                } else it.value + " " + it.unit
                val indicatorHolder =
                    IndicatorHolder(null, null, null, false, value)
                outputMap?.put("DATA", indicatorHolder)
            }
            if (it.system_name == "minute_cost" && !outputMap.containsKey("VOICE")) {
                Log.d("HERE", "VOICES2")
                val value = if (it.value.trim() == "Безлимит") {
                    it.value
                } else it.value + " " + it.unit
                val indicatorHolder =
                    IndicatorHolder(null, null, null, false, value)
                outputMap?.put("VOICE", indicatorHolder)
            }
            if (it.system_name == "sms_cost" && !outputMap.containsKey("SMS")) {
                Log.d("HERE", "SMS2")
                val value = if (it.value.trim() == "Безлимит") {
                    it.value
                } else it.value + " " + it.unit
                val indicatorHolder =
                    IndicatorHolder(null, null, null, false, value)
                outputMap?.put("SMS", indicatorHolder)
            }
        }
        return outputMap
    }
}