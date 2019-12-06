package kz.atc.mobapp.models.main

import kz.atc.mobapp.models.CatalogTariffResponse
import kz.atc.mobapp.models.ExchangeResponse
import kz.atc.mobapp.models.RemainsResponse
import kz.atc.mobapp.models.TariffResponse

data class MainPagaAccumData(
    var phoneNumber : String? = null,
    var tariffData: TariffResponse? = null,
    var chargeDate: String? = null,
    var balance: Double? = null,
    var remains: List<RemainsResponse>? = null,
    var catalogTariff: CatalogTariffResponse? = null,
    var indicatorHolder: MutableMap<String, IndicatorHolder>? = null,
    var subExchange: ExchangeResponse? = null
)