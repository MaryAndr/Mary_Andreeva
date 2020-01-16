package kz.atc.mobapp.presenters.interactors

import android.content.Context
import android.util.Log
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.functions.Function4
import io.reactivex.functions.Function5
import kz.atc.mobapp.api.AuthServices
import kz.atc.mobapp.api.SubscriberServices
import kz.atc.mobapp.models.*
import kz.atc.mobapp.models.catalogTariff.CatalogTariffResponse
import kz.atc.mobapp.models.main.*
import kz.atc.mobapp.states.main.*
import kz.atc.mobapp.utils.*
import java.util.*

class SubscriberInteractor(ctx: Context) {


    val subService by lazy {
        SubscriberServices.create(ctx)
    }

    private val userService by lazy {
        AuthServices.create()
    }

    private val mapOfRegions =
        mapOf(
            0 to "СврдО",
            3 to "КургО",
            4 to "ХМАО",
            5 to "ЯНАО"
        )


    fun getTariffs(): Observable<ChangeTariffState> {
        val subTariff = subService.getSubTariff().onErrorReturn {
            null
        }

        val subAvalTariff = subService.getAvailableTariffs().onErrorReturn {
            mutableListOf()
        }

        val subServices = subService.getServicesList().onErrorReturn { mutableListOf() }

        return Observable.combineLatest(
            subTariff,
            subAvalTariff,
            subServices,
            Function3 { subTariffResp, subAvalTariffResp, subServicesResponse ->

                val currentTariff = TariffShow()
                var allTariffsInfo: CatalogTariffResponse? = null

                val tariffIds = subAvalTariffResp.joinToString { it.id.toString() }
                userService.getCatalogTariff(tariffIds).flatMap { tariffResp ->
                    val changeTariffMainData = mutableListOf<TariffShow>()
                    allTariffsInfo = tariffResp
                    tariffResp.tariffs.forEach { tariff ->
                        var curTariff = TariffShow()
                        val catResp =
                            CatalogTariffResponse(tariffResp.tariffs.filter { it.id == tariff.id }.toMutableList())
                        val curAboutData = MyTariffAboutData(subTariffResp, catResp, subServicesResponse)
                        curTariff.aboutData = curAboutData
                        curTariff.category =
                            tariff.attributes.firstOrNull { it.system_name == "additional_categories" }
                                ?.value
                        if (curTariff.category == null) {
                            curTariff.category = "Без категории"
                        }
                        curTariff.name = tariff.name
                        curTariff.isCurrent = subTariffResp.tariff.id == tariff.id

                        if (tariff.id in mutableListOf(
                                14,
                                26,
                                27,
                                28
                            )
                        ) {
                            curTariff.dataValueUnit = subTariffResp.tariff.constructor.data
                            curTariff.smsValueUnit = subTariffResp.tariff.constructor.sms
                            curTariff.voiceValueUnit = subTariffResp.tariff.constructor.min
                            curTariff.price = subTariffResp.tariff.constructor.abon
                            curTariff.description = TextConverter().descriptionBuilder(
                                subTariffResp?.tariff?.constructor?.min!!.substringBefore(","),
                                subTariffResp?.tariff?.constructor?.data,
                                subTariffResp?.tariff?.constructor?.sms!!.substringBefore(",")
                            )
                        } else {
                            val isSubFee =
                                tariff.attributes?.firstOrNull { it.system_name == "subscription_fee" }?.value != "0"

                            curTariff.price = tariff.attributes?.firstOrNull { it.system_name == "subscription_fee" }?.value

                            curTariff.description =
                                tariff.attributes?.firstOrNull { it.system_name == "short_description" }
                                    ?.value

                            if (isSubFee) {
                                if (!tariff.attributes?.firstOrNull { it.system_name == "internet_gb_count" }?.value?.orEmpty().isNullOrEmpty()) {
                                    curTariff.dataValueUnit =
                                        tariff.attributes?.firstOrNull { it.system_name == "internet_gb_count" }?.value.orEmpty() + " " + tariff.attributes?.firstOrNull { it.system_name == "internet_gb_count" }?.unit.orEmpty()
                                } else {
                                    curTariff.dataValueUnit = null
                                }
                                if (!tariff.attributes?.firstOrNull { it.system_name == "minutes_count" }?.value?.orEmpty().isNullOrEmpty()) {
                                    curTariff.voiceValueUnit =
                                        tariff.attributes?.firstOrNull { it.system_name == "minutes_count" }?.value.orEmpty() + " " + tariff.attributes?.firstOrNull { it.system_name == "minutes_count" }?.unit.orEmpty()
                                } else {
                                    curTariff.voiceValueUnit = null
                                }
                                if (!tariff.attributes?.firstOrNull { it.system_name == "sms_count" }?.value?.orEmpty().isNullOrEmpty()) {
                                    curTariff.smsValueUnit =
                                        tariff.attributes?.firstOrNull { it.system_name == "sms_count" }?.value.orEmpty() + " " + tariff.attributes?.firstOrNull { it.system_name == "sms_count" }?.unit.orEmpty()
                                } else {
                                    curTariff.smsValueUnit = null
                                }
                            } else {
                                if (!tariff.attributes?.firstOrNull { it.system_name == "internet_mb_cost" }?.value?.orEmpty().isNullOrEmpty()) {
                                    curTariff.dataValueUnit =
                                        tariff.attributes?.firstOrNull { it.system_name == "internet_mb_cost" }?.value.orEmpty() + " " + tariff.attributes?.firstOrNull { it.system_name == "internet_mb_cost" }?.unit.orEmpty()
                                } else {
                                    curTariff.dataValueUnit = null
                                }
                                if (!tariff.attributes?.firstOrNull { it.system_name == "minute_cost" }?.value?.orEmpty().isNullOrEmpty()) {
                                    curTariff.voiceValueUnit =
                                        tariff.attributes?.firstOrNull { it.system_name == "minute_cost" }?.value.orEmpty() + " " + tariff.attributes?.firstOrNull { it.system_name == "minute_cost" }?.unit.orEmpty()
                                } else {
                                    curTariff.voiceValueUnit = null
                                }
                                if (!tariff.attributes?.firstOrNull { it.system_name == "sms_cost" }?.value?.orEmpty().isNullOrEmpty()) {
                                    curTariff.smsValueUnit =
                                        tariff.attributes?.firstOrNull { it.system_name == "sms_cost" }?.value.orEmpty() + " " + tariff.attributes?.firstOrNull { it.system_name == "sms_cost" }?.unit.orEmpty()
                                } else {
                                    curTariff.smsValueUnit = null
                                }
                            }
                        }

                        changeTariffMainData.add(curTariff)
                    }
                    Observable.just(ChangeTariffState.MainDataLoaded(changeTariffMainData))
                }.blockingFirst()

//                currentTariff.id = subTariffResp.tariff.id.toString()
//                currentTariff.category =
//                    allTariffsInfo?.tariffs?.firstOrNull { it.id == subTariffResp.tariff.id }
//                        ?.attributes?.firstOrNull { param -> param.system_name == "additional_categories" }
//                        ?.value
//
//                if (subTariffResp.tariff.id in mutableListOf(
//                        14,
//                        26,
//                        27,
//                        28
//                    )
//                ) {
//                    subService.getServicesList().flatMap {
//
//                    }
//                }


            }
        )

    }


    fun getEnabledServices(isExist: Boolean): Observable<MutableList<ServicesListShow>> {
        val subInfo = subService.getSubInfo().onErrorReturn {
            null
        }

        val subServices = subService.getServicesList().onErrorReturn { mutableListOf() }

        val subAllService = if (isExist) {
            Observable.just(mutableListOf<ServicesListResponse>())
        } else {
            subService.getAllServicesList().onErrorReturn { mutableListOf() }
        }

        return Observable.combineLatest(
            subInfo,
            subServices,
            subAllService,
            Function3 { subInfoResponse, subServiceResponse, subAllServiceResponse ->

                val servicesList: MutableList<ServicesListShow> = mutableListOf()


                val subServicesIds = when {
                    !isExist && subAllServiceResponse.isNotEmpty() -> "${subServiceResponse.joinToString { it.id.toString() }},${subAllServiceResponse.joinToString { it.id.toString() }}"
                    else -> subServiceResponse.joinToString { it.id.toString() }
                }

                userService.getCatalogService(
                    subServicesIds,
                    mapOfRegions[subInfoResponse.region.id]
                ).map<MutableList<ServicesListShow>> { catResp ->
                    catResp.services.forEach {
                        val serviceShow = ServicesListShow()
                        val subServicesListInstance =
                            subServiceResponse.firstOrNull { pred -> pred.id == it.id }
                        val subServicesAllListInstance =
                            subAllServiceResponse.firstOrNull { pred -> pred.id == it.id }
                        serviceShow.id = it.id.toString()
                        if (subServicesListInstance != null) {
                            serviceShow.serviceName =
                                subServicesListInstance.name
                            serviceShow.description =
                                it.attributes.firstOrNull { predicate -> predicate.system_name == "short_description" }
                                    ?.value.orEmpty()

                            if (subServicesListInstance.interval?.type == null) {
                                serviceShow.price =
                                    subServicesListInstance
                                        .priceOn.toString()
                            } else {
                                serviceShow.price =
                                    subServicesListInstance
                                        .price.toString()
                            }
                            serviceShow.interval = when {
                                subServicesListInstance.interval?.type == "day" -> "сутки"
                                subServicesListInstance.interval?.type == "month" -> "месяц"
                                else -> "подключение"
                            }

                            if (subServicesListInstance.unlock) {
                                serviceShow.toggleState = ToggleButtonState.ActiveAndEnabled
                            } else {
                                serviceShow.toggleState = ToggleButtonState.ActiveAndDisabled
                            }
                            serviceShow.category =
                                it.attributes.firstOrNull { pred -> pred.system_name == "main_category" }
                                    ?.value
                            serviceShow.isExistOnSub = true

                            servicesList.add(serviceShow)
                        }
                        if (subServicesAllListInstance != null) {
                            serviceShow.serviceName =
                                subServicesAllListInstance.name
                            serviceShow.description =
                                it.attributes.firstOrNull { predicate -> predicate.system_name == "short_description" }
                                    ?.value.orEmpty()

                            serviceShow.activPrice =
                                it.attributes.firstOrNull { predicate -> predicate.system_name == "activation_price" }
                                    ?.value.orEmpty()
                            serviceShow.subFee =
                                it.attributes.firstOrNull { predicate -> predicate.system_name == "subscription_fee" }
                                    ?.value.orEmpty()

                            if (subServicesAllListInstance.interval?.type == null) {
                                serviceShow.price =
                                    subServicesAllListInstance
                                        .priceOn.toString()
                            } else {
                                serviceShow.price =
                                    subServicesAllListInstance
                                        .price.toString()
                            }
                            serviceShow.interval = when {
                                subServicesAllListInstance.interval?.type == "day" -> "сутки"
                                subServicesAllListInstance.interval?.type == "month" -> "месяц"
                                else -> "подключение"
                            }
                            if (subServicesAllListInstance.unlock) {
                                serviceShow.toggleState = ToggleButtonState.InactiveAndEnabled
                            } else {
                                serviceShow.toggleState = ToggleButtonState.NotShown
                            }
                            serviceShow.category =
                                it.attributes.firstOrNull { pred -> pred.system_name == "main_category" }
                                    ?.value
                            serviceShow.isExistOnSub = false

                            servicesList.add(serviceShow)
                        }
                    }
                    servicesList
                }.subscribe()

                servicesList
            })

    }


    fun getMyTariffMainData(): Observable<MyTariffPartialState> {
        val subTariff = subService.getSubTariff().onErrorReturn {
            null
        }

        val subInfo = subService.getSubInfo().onErrorReturn {
            null
        }

        val subRemains = subService.getSubRemains().onErrorReturn {
            mutableListOf()
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
            subRemains,
            Function4 { subTariffResponse, subInfoResponse, transHistoryResponse, subRemainsResponse ->
                val mainData = MyTariffMainData(subTariffResponse)
                userService.getCatalogTariff(subTariffResponse.tariff.id.toString()).flatMap {
                    mainData.catalogTariff = it
                    Observable.just(mainData)
                }.onErrorReturn {
                    mainData.catalogTariff = null
                    mainData
                }.blockingFirst()

                subService.getServicesList().subscribe { list ->
                    mainData.servicesListOriginal = list
                    userService.getCatalogService(
                        list.joinToString { it.id.toString() },
                        mapOfRegions[subInfoResponse.region.id]
                    ).flatMap { serviceDetails ->
                        serviceDetails.services.forEach {
                            val serviceShow = ServicesListShow()
                            serviceShow.id = it.id.toString()
                            serviceShow.serviceName = list.first { pred -> pred.id == it.id }.name
                            serviceShow.description =
                                it.attributes.first { predicate -> predicate.system_name == "short_description" }
                                    ?.value.orEmpty()
                            serviceShow.price =
                                list.first { pred -> pred.id == it.id }.price.toString()
                            mainData.servicesList.add(serviceShow)
                        }
                        Observable.just(mainData)
                    }.onErrorReturn {
                        mainData.servicesList = mutableListOf()
                        mainData
                    }.blockingFirst()
                }

                mainData.indicatorHolder = calculateIndicators(null, subRemainsResponse, null)
                mainData.indicatorModels =
                    accumulateIndicators(
                        subTariffResponse,
                        subRemainsResponse,
                        transHistoryResponse
                    )

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
                    userService.getCatalogTariff(subT.tariff.id.toString()).flatMap { catTar ->
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

    private fun accumulateIndicators(
        tariffs: TariffResponse?,
        remains: List<RemainsResponse>?,
        transferredHistory: List<TransferredHistoryResponse>?
    ): IndicatorsModel {
        var dataIndicators = mutableListOf<IndicatorHolder>()
        var voiceIndicators = mutableListOf<IndicatorHolder>()
        var smsIndicators = mutableListOf<IndicatorHolder>()


        transferredHistory?.forEach {
            when {
                it.type == "DATA" -> {
                    if (it.amount != null && it.amount > 0) {
                        val rest = it.amount
                        val total = it.amount
                        val name = "Перенесенные остатки"
                        var indicatorData = IndicatorHolder(
                            rest,
                            total,
                            100,
                            false,
                            optionsName = name,
                            type = "DATA"
                        )
                        dataIndicators.add(indicatorData)
                    }
                    if (it.exchange != null && it.exchange > 0) {
                        val rest = it.exchange.toInt()
                        val total = it.exchange.toInt()
                        val name = "Количество обменянных ГБ"
                        var indicatorData = IndicatorHolder(
                            rest,
                            total,
                            100,
                            false,
                            optionsName = name,
                            type = "DATA"
                        )
                        dataIndicators.add(indicatorData)
                    }

                }
                it.type == "VOICE" -> {
                    if (it.amount != null && it.amount > 0) {
                        val rest = it.amount
                        val total = it.amount
                        val name = "Перенесенные остатки"
                        var indicatorData = IndicatorHolder(
                            rest,
                            total,
                            100,
                            false,
                            optionsName = name,
                            type = "VOICE"
                        )
                        voiceIndicators.add(indicatorData)
                    }
                    if (it.exchange != null && it.exchange > 0) {
                        val rest = it.exchange.toInt()
                        val total = it.exchange.toInt()
                        val name = "Количество обменянных Мин."
                        var indicatorData = IndicatorHolder(
                            rest,
                            total,
                            100,
                            false,
                            optionsName = name,
                            type = "VOICE"
                        )
                        voiceIndicators.add(indicatorData)
                    }

                }
                it.type == "SMS" -> {
                    if (it.amount != null && it.amount > 0) {
                        val rest = it.amount
                        val total = it.amount
                        val name = "Перенесенные остатки"
                        var indicatorData = IndicatorHolder(
                            rest,
                            total,
                            100,
                            false,
                            optionsName = name,
                            type = "SMS"
                        )
                        smsIndicators.add(indicatorData)
                    }
                    if (it.exchange != null && it.exchange > 0) {
                        val rest = it.exchange.toInt()
                        val total = it.exchange.toInt()
                        val name = "Количество обменянных SMS"
                        var indicatorData = IndicatorHolder(
                            rest,
                            total,
                            100,
                            false,
                            optionsName = name,
                            type = "SMS"
                        )
                        smsIndicators.add(indicatorData)
                    }

                }
            }
        }



        remains?.forEach { remain ->
            when {
                remain.type == "DATA" -> {
                    var rest = remain.rest_amount
                    var total = remain.total_amount
                    var name = if (remain.services.primary) {
                        "По условиям тарифа"
                    } else {
                        remain.services.name.orEmpty()
                    }
                    var dueDate = ""
                    if (remain.due_date == "2999-12-31") {
                        dueDate = remain.due_date
                    }
                    var indicatorData: IndicatorHolder?
                    indicatorData = if (rest != 0 && total != 0) {
                        IndicatorHolder(
                            rest,
                            total,
                            MathUtils().calculatePercent(rest, total),
                            false, optionsName = name, dueDate = dueDate,
                            type = "DATA"
                        )
                    } else {
                        IndicatorHolder(
                            rest,
                            total,
                            0,
                            false,
                            optionsName = name,
                            dueDate = dueDate,
                            type = "DATA"
                        )
                    }
                    dataIndicators.add(indicatorData)
                }
                remain.type == "VOICE" -> {
                    var rest = remain.rest_amount
                    var total = remain.total_amount
                    var name = if (remain.services.primary) {
                        "По условиям тарифа"
                    } else {
                        remain.services.name.orEmpty()
                    }
                    var dueDate = ""
                    if (remain.due_date == "2999-12-31") {
                        dueDate = remain.due_date
                    }
                    var indicatorData: IndicatorHolder?
                    indicatorData = if (rest != 0 && total != 0) {
                        IndicatorHolder(
                            rest,
                            total,
                            MathUtils().calculatePercent(rest, total),
                            false, optionsName = name, dueDate = dueDate, type = "VOICE"
                        )
                    } else {
                        IndicatorHolder(
                            rest,
                            total,
                            0,
                            false,
                            optionsName = name,
                            dueDate = dueDate,
                            type = "VOICE"
                        )
                    }
                    voiceIndicators.add(indicatorData)
                }
                remain.type == "SMS" -> {
                    var rest = remain.rest_amount
                    var total = remain.total_amount
                    var name = if (remain.services.primary) {
                        "По условиям тарифа"
                    } else {
                        remain.services.name.orEmpty()
                    }
                    var dueDate = ""
                    if (remain.due_date == "2999-12-31") {
                        dueDate = remain.due_date
                    }

                    var indicatorData: IndicatorHolder?
                    indicatorData = if (rest != 0 && total != 0) {
                        IndicatorHolder(
                            rest,
                            total,
                            MathUtils().calculatePercent(rest, total),
                            false, optionsName = name, dueDate = dueDate, type = "SMS"
                        )
                    } else {
                        IndicatorHolder(
                            rest,
                            total,
                            0,
                            false,
                            optionsName = name,
                            dueDate = dueDate,
                            type = "SMS"
                        )
                    }
                    smsIndicators.add(indicatorData)
                }
            }

        }

        tariffs?.options?.forEach { option ->
            when {
                option.type == "DATA" -> {
                    val indicatorHolder =
                        IndicatorHolder(null, null, null, true, null, option.name, null)
                    dataIndicators.add(indicatorHolder)
                }
                option.type == "VOICE" -> {
                    val indicatorHolder =
                        IndicatorHolder(null, null, null, true, null, option.name, null)
                    voiceIndicators.add(indicatorHolder)
                }
                option.type == "SMS" -> {
                    val indicatorHolder =
                        IndicatorHolder(null, null, null, true, null, option.name, null)
                    smsIndicators.add(indicatorHolder)
                }
            }
        }

        return IndicatorsModel(dataIndicators, voiceIndicators, smsIndicators)
    }

    private fun calculateIndicators(
        tariff: TariffResponse?,
        remains: List<RemainsResponse>?,
        catalogTariff: CatalogTariffResponse?
    ): MutableMap<String, IndicatorHolder> {
        val outputMap: MutableMap<String, IndicatorHolder> = mutableMapOf()
        remains?.filter { predicate -> predicate.services.primary }?.forEach {
            if (it.type == "DATA") {
                var rest = it.rest_amount
                var total = it.total_amount
                Log.d("HERE", "DATA $rest and $total")
                var indicatorData: IndicatorHolder?
                indicatorData = if (rest == 0 && total == 0) {
                    IndicatorHolder(rest, total, 0, false)
                } else {
                    IndicatorHolder(
                        rest,
                        total,
                        MathUtils().calculatePercent(rest, total),
                        false
                    )

                }
                outputMap?.put("DATA", indicatorData)
            }
            if (it.type == "VOICE") {
                Log.d("HERE", "VOICES")
                var rest = it.rest_amount
                var total = it.total_amount
                var indicatorData: IndicatorHolder?
                indicatorData = if (rest == 0 && total == 0) {
                    IndicatorHolder(rest, total, 0, false)
                } else {
                    IndicatorHolder(
                        rest,
                        total,
                        MathUtils().calculatePercent(rest, total),
                        false
                    )
                }
                outputMap?.put("VOICE", indicatorData)
            }
            if (it.type == "SMS") {
                Log.d("HERE", "SMS")
                var rest = it.rest_amount
                var total = it.total_amount
                var indicatorData: IndicatorHolder?
                indicatorData = if (rest == 0 && total == 0) {
                    IndicatorHolder(rest, total, 0, false)
                } else {
                    IndicatorHolder(
                        rest,
                        total,
                        MathUtils().calculatePercent(rest, total),
                        false
                    )
                }
                outputMap?.put("SMS", indicatorData)
            }
        }
        tariff?.options?.filter { predicate -> predicate.primary }?.forEach {

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