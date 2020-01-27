package ru.filit.motiv.app.models.main

import ru.filit.motiv.app.models.CatalogTariffResponse
import ru.filit.motiv.app.models.ExchangeResponse
import ru.filit.motiv.app.models.RemainsResponse
import ru.filit.motiv.app.models.TariffResponse

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