package ru.filit.motiv.app.presenters.interactors

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.functions.Function4
import io.reactivex.functions.Function5
import retrofit2.HttpException
import ru.filit.motiv.app.api.AuthServices
import ru.filit.motiv.app.api.SubscriberServices
import ru.filit.motiv.app.models.*
import ru.filit.motiv.app.models.catalogTariff.CatalogTariffResponse
import ru.filit.motiv.app.models.main.*
import ru.filit.motiv.app.states.main.*
import ru.filit.motiv.app.utils.*
import java.util.*
import java.util.concurrent.TimeUnit

class SubscriberInteractor(val ctx: Context) {


    val subService by lazy {
        SubscriberServices.create(ctx)
    }

    val userService by lazy {
        AuthServices.create(ctx)
    }

    val gson = Gson()

    private val mapOfRegions =
        mapOf(
            0 to "СврдО",
            3 to "КургО",
            4 to "ХМАО",
            5 to "ЯНАО"
        )

    private val appIsDeprecated = "99999"

    fun getSettingsMainData(): Observable<SettingsState> {

        val subInfo = subService.subscriberInfo()

        val subStatus = subService.subscriberStatus().onErrorReturn { null }


        return Observable.combineLatest(
            subInfo,
            subStatus,
            BiFunction<SubscriberInfoResponse, SubscriberStatusResponse, Observable<SettingsState>> { subInfoResponse, subStatusResponse ->
                val contract = if (subInfoResponse.contract_date != null) {
                    "${subInfoResponse.contract_number} от ${TextConverter().getFormattedDate(subInfoResponse.contract_date)}"
                } else {
                    subInfoResponse.contract_number
                }

                val dataModel = SettingsDataModel(
                    subInfoResponse.full_name,
                    subStatusResponse.status.id,
                    subInfoResponse.msisdn,
                    subInfoResponse.personal_account.toString(),
                    contract,
                    subInfoResponse.region.name
                )
                Observable.just(SettingsState.MainDataLoaded(dataModel))
            }).flatMap { it }.onErrorReturn { error->
            if (error is HttpException) {
                val errorBody = error.response()!!.errorBody()
                val adapter =
                    gson.getAdapter<ErrorJson>(ErrorJson::class.java!!)
                val errorObj = adapter.fromJson(errorBody!!.string())
                SettingsState.ShowErrorMessage(errorObj.error_description)
            }else {
                SettingsState.ShowErrorMessage("Что-то пошло не так, возможно у вас пропало интернет соединение.")
            }
        }.retry()

    }


    fun getTariffs(): Observable<ChangeTariffState> {
        val subTariff = subService.getSubTariff()

        val subAvalTariff = subService.getAvailableTariffs().onErrorReturn {
            if (it is HttpException && it.code() == 404) {
                mutableListOf()
            } else {
                throw  it
            }
        }

        val subServices = subService.getServicesList().onErrorReturn {
            if (it is HttpException && it.code() == 404) {
                mutableListOf()
            } else {
                throw  it
            }
        }

        return Observable.combineLatest(
            subTariff,
            subAvalTariff,
            subServices,
            Function3<TariffResponse, List<AvailableTariffs>, List<ServicesListResponse>, Observable<ChangeTariffState>> { subTariffResp, subAvalTariffResp, subServicesResponse ->

                val currentTariff = TariffShow()
                var allTariffsInfo: CatalogTariffResponse? = null

                val tariffIds =
                    "${subAvalTariffResp.joinToString { it.id.toString() }},${subTariffResp.tariff.id}"
                userService.getCatalogTariff(tariffIds).flatMap { tariffResp ->
                    val changeTariffMainData = mutableListOf<TariffShow>()
                    allTariffsInfo = tariffResp
                    tariffResp.tariffs.forEach { tariff ->
                        var curTariff = TariffShow()
                        val catResp =
                            CatalogTariffResponse(tariffResp.tariffs.filter { it.id == tariff.id }.toMutableList())
                        val curAboutData =
                            MyTariffAboutData(subTariffResp, catResp, subServicesResponse)
                        curTariff.aboutData = curAboutData
                        curTariff.category =
                            tariff.attributes.firstOrNull { it.system_name == "main_category" }
                                ?.value
                        if (tariff.id == subTariffResp.tariff.id) {
                            curTariff.category = null
                            curTariff.isCurrent = true
                        } else {
                            if (curTariff.category == "Новинки") {
                                curTariff.isNew = true
                                if (tariff.attributes.firstOrNull { it.system_name == "additional_categories" }
                                        ?.value != null) {
                                    curTariff.category =
                                        tariff.attributes.firstOrNull { it.system_name == "additional_categories" }
                                            ?.value
                                } else {
                                    curTariff.category = "Без категории"
                                }
                            }
                        }
                        curTariff.name = tariff.name

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
                                subTariffResp.tariff.constructor.min?.substringBefore(","),
                                subTariffResp?.tariff?.constructor?.data,
                                subTariffResp?.tariff?.constructor?.sms?.substringBefore(",")
                            )
                        } else {
                            val isSubFee =
                                tariff.attributes?.firstOrNull { it.system_name == "subscription_fee" }?.value != "0"

                            curTariff.price =
                                tariff.attributes?.firstOrNull { it.system_name == "subscription_fee" }
                                    ?.value
                            curTariff.interval = when(tariff.attributes?.firstOrNull { it.system_name == "write_off_period" }?.value) {
                                "Посуточно" -> "сутки"
                                "Ежемесячно" -> "месяц"
                                else -> "месяц"
                            }


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
                }

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
        ).flatMap { it }.onErrorReturn { error->
            if (error is HttpException) {
                val errorBody = error.response()!!.errorBody()
                val adapter =
                    gson.getAdapter<ErrorJson>(ErrorJson::class.java!!)
                val errorObj = adapter.fromJson(errorBody!!.string())
                ChangeTariffState.ShowErrorMessage(errorObj.error_description)
            }else {
                ChangeTariffState.ShowErrorMessage("Что-то пошло не так, возможно у вас пропало интернет соединение.")
            }
        }.retry()

    }

    fun getEnabledServices(isExist: Boolean):  Observable<ServicesPartialState> {
        val subInfo = subService.getSubInfo()

        val subServices = subService.getServicesList()

        val subAllService = if (isExist) {
            Observable.just(mutableListOf<ServicesListResponse>())
        } else {
            subService.getAllServicesList().onErrorReturn {
                if (it is HttpException && it.code() == 404) {
                    mutableListOf()
                } else {
                    throw  it
                }
            }
        }

        return Observable.combineLatest(
            subInfo,
            subServices,
            subAllService,
            Function3<SubInfoResponse, List<ServicesListResponse>, List<ServicesListResponse>, Observable<MutableList<ServicesListShow>>> { subInfoResponse, subServiceResponse, subAllServiceResponse ->

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

                                serviceShow.price =
                                    subServicesListInstance
                                        .price.toString()

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

                            serviceShow.activPrice = subServicesAllListInstance.price_on.toString()

                            serviceShow.subFee =
                                subServicesAllListInstance
                                    .price.toString()

                            if (subServicesAllListInstance.interval?.type == null) {
                                serviceShow.price = subServicesAllListInstance.price_on.toString()
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

                            if (serviceShow.toggleState != ToggleButtonState.NotShown) {
                                servicesList.add(serviceShow)
                            }
                        }
                    }
                    servicesList
                }.subscribe()

                Observable.just(servicesList)
            }).flatMap {it}
            .flatMap {it.sortByDescending{servicesListShow -> servicesListShow.price}
                if (isExist) {

                    Observable.just(ServicesPartialState.FetchEnabledService(it))
                } else {
                    Observable.just(ServicesPartialState.FetchAllService(it))
                }

        }.onErrorReturn { error->
                if (error is HttpException) {
                    val errorBody = error.response()!!.errorBody()
                    val adapter =
                        gson.getAdapter<ErrorJson>(ErrorJson::class.java!!)
                    val errorObj = adapter.fromJson(errorBody!!.string())
                    ServicesPartialState.ShowErrorMessage(errorObj.error_description)
                }else {
                    ServicesPartialState.ShowErrorMessage("Что-то пошло не так, возможно у вас пропало интернет соединение.")
                }
            }.retry()
    }


    fun getMyTariffMainData(): Observable<MyTariffPartialState> {
        if (!isConnect(ctx)){
            return Observable.just(MyTariffPartialState.InternetState(false))
        }
        val subTariff = subService.getSubTariff()

        val subInfo = subService.getSubInfo()

        val subRemains = subService.getSubRemains().onErrorReturn {
            if (it is HttpException && it.code() == 404) {
                mutableListOf()
            } else {
                throw  it
            }
        }

        val transHistory = subService.getTransferedHistory().onErrorReturn {
            if (it is HttpException && it.code() == 404) {
                mutableListOf()
            } else {
                throw  it
            }
        }

        val exchangeInfo = subService.getExchangeInfo()

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
            exchangeInfo,
            Function5<TariffResponse, SubInfoResponse, List<TransferredHistoryResponse>, List<RemainsResponse>, ExchangeResponse, Observable<MyTariffPartialState>> { subTariffResponse, subInfoResponse, transHistoryResponse, subRemainsResponse, exResponse ->
                val mainData = MyTariffMainData(subTariffResponse)
                mainData.exchangeInfo = exResponse
                userService.getCatalogTariff(subTariffResponse.tariff.id.toString()).flatMap {
                    mainData.catalogTariff = it
                    Observable.just(mainData)
                }.onErrorReturn {
                    mainData.catalogTariff = null
                    mainData
                }.blockingFirst()

                subService.getServicesList().subscribe ({ list ->
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
                                it.attributes.firstOrNull { predicate -> predicate.system_name == "short_description" }
                                    ?.value.orEmpty()
                            serviceShow.price =
                                list.first { pred -> pred.id == it.id }.price.toString()
                            if(list.first {pred -> pred.id==it.id}.unlock){
                                serviceShow.toggleState = ToggleButtonState.ActiveAndEnabled
                            }else{ serviceShow.toggleState = ToggleButtonState.ActiveAndDisabled}
                            serviceShow.interval = when(list.firstOrNull{pred->
                                pred.id == it.id}?.interval?.type){
                                "day" -> "сутки"
                                "month" -> "месяц"
                                else -> "подключение"
                            }

                            mainData.servicesList.add(serviceShow)
                        }
                        mainData.servicesList.sortByDescending { servicesListShow -> servicesListShow.price }
                        Observable.just(mainData)
                    }.onErrorReturn {
                        it.printStackTrace()
                        mainData.servicesList = mutableListOf()
                        mainData
                    }.blockingFirst()

                },{error->MyTariffPartialState.ShowErrorMessage(error.localizedMessage)})

                mainData.indicatorHolder = calculateIndicators(null, subRemainsResponse, null)
                mainData.indicatorModels =
                    accumulateIndicators(
                        subTariffResponse,
                        subRemainsResponse,
                        transHistoryResponse
                    )

                Observable.just(MyTariffPartialState.MainDataLoadedState(mainData))
            }).flatMap { it }.onErrorReturn { error->
            if (error is HttpException) {
                val errorBody = error.response()!!.errorBody()
                val adapter =
                    gson.getAdapter<ErrorJson>(ErrorJson::class.java!!)
                val errorObj = adapter.fromJson(errorBody!!.string())
                if (error.code() == 403 && errorObj.error_code == appIsDeprecated) {
                    MyTariffPartialState.ShowErrorMessage(
                        errorObj.error_description,
                        appIsDeprecated = true
                    )
                } else {
                    MyTariffPartialState.ShowErrorMessage(errorObj.error_description)
                }
            } else {
                MyTariffPartialState.ShowErrorMessage("Что-то пошло не так, возможно у вас пропало интернет соединение.")
            }
        }.retry()

    }


    fun getReplenishmentData(period: String): Observable<CostAndReplenishmentPartialState> {
        if (!isConnect(ctx = ctx)){
            return Observable.just(CostAndReplenishmentPartialState.InternetState(active = false))
        }
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

        val monthAgo = Calendar.getInstance()
        monthAgo.add(Calendar.DAY_OF_MONTH, -30)
        val period =
        "${TimeUtils().dateToString(monthAgo)}-${TimeUtils().dateToString(Calendar.getInstance())}"
        return  Observable.just(CostsEmailState.MsisdnShown(period))
    }



    fun costsMainData(): Observable<CostAndReplenishmentPartialState> {
        if (!isConnect(ctx = ctx)){
            return Observable.just(CostAndReplenishmentPartialState.InternetState(false))
        }
        val subInfo = subService.getSubInfo()

        val subBalance = subService.getSubBalance()

        val subTariff = subService.getSubTariff()

        val subServices = subService.getAllServicesList()

        return Observable.combineLatest(
            subInfo,
            subTariff,
            subBalance,
            subServices,
            Function4<SubInfoResponse, TariffResponse,SubBalanceResponse, List<ServicesListResponse>, Observable<CostAndReplenishmentPartialState>> {subInfo, subT, subBal, subServiceResponse ->
                val accumData = MainPagaAccumData()
                accumData.tariffData = subT
                accumData.phoneNumber = subInfo.msisdn
                accumData.balance = subBal.value
                accumData.isDetalization = subServiceResponse.any { it.id == 1322 }
                if (accumData.isDetalization) {
                    accumData.costDetalization = subServiceResponse.first { it.id == 1322 }.price_on
                }
                Observable.just(CostAndReplenishmentPartialState.ShowMainDataState(accumData) as CostAndReplenishmentPartialState)
            }).flatMap { it }.onErrorReturn { error->
            if (error is HttpException) {
                val errorBody = error.response()!!.errorBody()
                val adapter =
                    gson.getAdapter<ErrorJson>(ErrorJson::class.java!!)
                val errorObj = adapter.fromJson(errorBody!!.string())
                if (error.code()==403&&errorObj.error_code==appIsDeprecated){
                    CostAndReplenishmentPartialState.ShowErrorState(errorObj.error_description, appIsDeprecated = true)
                }else {
                    CostAndReplenishmentPartialState.ShowErrorState(errorObj.error_description)
                }
            }else {
                CostAndReplenishmentPartialState.ShowErrorState("Что-то пошло не так, возможно у вас пропало интернет соединение.")
            }
        }.retry()

    }


    fun preLoadData(): Observable<MainPagePartialState> {
        if (!isConnect(ctx)){
            return Observable.just(MainPagePartialState.InternetState(false))
        }

        val subInfo = subService.getSubInfo()

        val subTariff = subService.getSubTariff()

        val subBalance = subService.getSubBalance()

        val subRemains = subService.getSubRemains().onErrorReturn { listOf() }

        val exchangeInfo = subService.getExchangeInfo()


        return Observable.combineLatest(
            subInfo,
            subTariff,
            subBalance,
            subRemains,
            exchangeInfo,
            Function5<SubInfoResponse,TariffResponse, SubBalanceResponse, List<RemainsResponse>,ExchangeResponse, Observable<MainPagePartialState>> { subInfo, subT, subBal, subRem, subEx ->
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
                       Observable.just(MainPagePartialState.ShowDataState(accumData) as MainPagePartialState)
                    }.onErrorReturn {
                        accumData.indicatorHolder = calculateIndicators(subT, subRem, null)
                        MainPagePartialState.ShowDataState(accumData)as MainPagePartialState
                    }
                } else {
                    accumData.indicatorHolder = calculateIndicators(subT, subRem, null)
                    Observable.just(MainPagePartialState.ShowDataState(accumData))
                }
            }).flatMap { it }.onErrorReturn{ error->
            if (error is HttpException) {
                val errorBody = error.response()!!.errorBody()
                val adapter =
                    gson.getAdapter<ErrorJson>(ErrorJson::class.java!!)
                val errorObj = adapter.fromJson(errorBody!!.string())
                if (error.code()==403&&errorObj.error_code==appIsDeprecated){
                    MainPagePartialState.ShowErrorMessage(errorObj.error_description, appIsDeprecated = true)

                }else {
                    MainPagePartialState.ShowErrorMessage(errorObj.error_description)
                }
            }else {
                MainPagePartialState.ShowErrorMessage("Что-то пошло не так, возможно у вас пропало интернет соединение.")
            }
        }
    }

    fun getFAQ (): Observable<FAQState>{
        if (!isConnect(ctx)){
            return Observable.just(FAQState.InternetState(false))
        }
        return subService.getFAQ().flatMap {
            val categoryQuestionsList = mutableListOf<CategoryQuestions>()
            it.forEach{faqResponse->
                categoryQuestionsList.add(faqResponse.toCategoryQuestionList())
            }
            Observable.just(FAQState.QuestionsLoaded(categoryQuestionsList) as FAQState)
        }.onErrorReturn{ error->
            if (error is HttpException) {
                val errorBody = error.response()!!.errorBody()
                val adapter =
                    gson.getAdapter<ErrorJson>(ErrorJson::class.java!!)
                val errorObj = adapter.fromJson(errorBody!!.string())
                if (error.code()==403&&errorObj.error_code==appIsDeprecated){
                    FAQState.ShowErrorMessage(errorObj.error_description)

                }else {
                    FAQState.ShowErrorMessage(errorObj.error_description)
                }
            }else {
                FAQState.ShowErrorMessage("Что-то пошло не так, возможно у вас пропало интернет соединение.")
            }
        }
    }

    private fun accumulateIndicators(
        tariffs: TariffResponse?,
        remains: List<RemainsResponse>?,
        transferredHistory: List<TransferredHistoryResponse>?
    ): IndicatorsModel {
        var dataIndicators = mutableListOf<IndicatorHolder>()
        var voiceIndicators = mutableListOf<IndicatorHolder>()
        var smsIndicators = mutableListOf<IndicatorHolder>()
        val textConverter = TextConverter()


        transferredHistory?.forEach {
            when {
                it.type == "DATA" -> {
                    if (it.amount != null && it.amount > 0) {
                        val rest = it.amount
                        val total = it.amount
                        var dueDate = ""
                        if (it.due_date != "2999-12-31") {
                            dueDate = textConverter.getFormattedDate(it.due_date)
                        }
                        val name = "Перенесенные остатки"
                        val indicatorData = IndicatorHolder(
                            rest,
                            total,
                            100,
                            false,
                            optionsName = name,
                            type = "DATA",
                            dueDate = dueDate
                        )
                        dataIndicators.add(indicatorData)
                    }
                    if (it.exchange != null && it.exchange > 0) {
                        val rest = it.exchange.toInt()
                        val total = it.exchange.toInt()
                        var dueDate = ""
                        if (it.due_date != "2999-12-31") {
                            dueDate = textConverter.getFormattedDate(it.due_date)
                        }
                        val name = "Обменные ГБ"
                        var indicatorData = IndicatorHolder(
                            rest,
                            total,
                            100,
                            false,
                            dueDate = dueDate,
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
                        var dueDate = ""
                        if (it.due_date != "2999-12-31") {
                            dueDate = textConverter.getFormattedDate(it.due_date)
                        }
                        val name = "Перенесенные остатки"
                        var indicatorData = IndicatorHolder(
                            rest,
                            total,
                            100,
                            false,
                            optionsName = name,
                            dueDate = dueDate,
                            type = "VOICE"
                        )
                        voiceIndicators.add(indicatorData)
                    }
                    if (it.exchange != null && it.exchange > 0) {
                        val rest = it.exchange.toInt()
                        val total = it.exchange.toInt()
                        val name = "Обменные Мин."
                        var dueDate = ""
                        if (it.due_date != "2999-12-31") {
                            dueDate = textConverter.getFormattedDate(it.due_date)
                        }
                        val indicatorData = IndicatorHolder(
                            rest,
                            total,
                            100,
                            false,
                            dueDate = dueDate,
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
                        var dueDate = ""
                        if (it.due_date != "2999-12-31") {
                            dueDate = textConverter.getFormattedDate(it.due_date)
                        }
                        var indicatorData = IndicatorHolder(
                            rest,
                            total,
                            100,
                            false,
                            optionsName = name,
                            type = "SMS",
                            dueDate = dueDate
                        )
                        smsIndicators.add(indicatorData)
                    }
                    if (it.exchange != null && it.exchange > 0) {
                        val rest = it.exchange.toInt()
                        val total = it.exchange.toInt()
                        val name = "Обменные SMS"
                        var dueDate = ""
                        if (it.due_date != "2999-12-31") {
                            dueDate = textConverter.getFormattedDate(it.due_date)
                        }
                        val indicatorData = IndicatorHolder(
                            rest,
                            total,
                            100,
                            false,
                            dueDate = dueDate,
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
                    val rest = remain.rest_amount
                    val total = remain.total_amount
                    val name = if (remain.services.primary) {
                        "По условиям тарифа"
                    } else {
                        remain.services.name.orEmpty()
                    }
                    var dueDate = ""
                    if (remain.due_date != "2999-12-31") {
                        dueDate = textConverter.getFormattedDate(remain.due_date)
                    }

                    if (rest != 0 || total != 0) {
                        val indicatorData=
                        IndicatorHolder(
                            rest,
                            total,
                            MathUtils().calculatePercent(rest, total),
                            false, optionsName = name, dueDate = dueDate,
                            type = "DATA"
                        )
                        dataIndicators.add(indicatorData)
                    }

                }
                remain.type == "VOICE" -> {
                    val rest:Int = remain.rest_amount
                    val total:Int = remain.total_amount
                    var name = if (remain.services.primary) {
                        "По условиям тарифа"
                    } else {
                        remain.services.name.orEmpty()
                    }
                    var dueDate = ""
                    if (remain.due_date != "2999-12-31") {
                        dueDate = textConverter.getFormattedDate(remain.due_date)
                    }
                    if (rest != 0 || total != 0) {
                        val indicatorData=
                            IndicatorHolder(
                                rest,
                                total,
                                MathUtils().calculatePercent(rest, total),
                                false, optionsName = name, dueDate = dueDate,
                                type = "VOICE"
                            )
                        voiceIndicators.add(indicatorData)
                    }
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
                    if (remain.due_date != "2999-12-31") {
                        dueDate = textConverter.getFormattedDate(remain.due_date)
                    }

                    if (rest != 0 || total != 0) {
                        val indicatorData =
                            IndicatorHolder(
                                rest,
                                total,
                                MathUtils().calculatePercent(rest, total),
                                false, optionsName = name, dueDate = dueDate,
                                type = "SMS"
                            )
                        smsIndicators.add(indicatorData)
                    }
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
                var dueDate = it.due_date
                Log.d("HERE", "DATA $rest and $total")
                var indicatorData: IndicatorHolder?
                indicatorData = if (rest == 0 && total == 0) {
                    IndicatorHolder(rest, total, 0, false, dueDate = dueDate)
                } else {
                    IndicatorHolder(
                        rest,
                        total,
                        MathUtils().calculatePercent(rest, total),
                        false,
                        dueDate = dueDate
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