package ru.filit.motiv.app.models.main

import ru.filit.motiv.app.models.ExchangeResponse
import ru.filit.motiv.app.models.TariffResponse
import ru.filit.motiv.app.models.catalogTariff.CatalogTariffResponse

data class MyTariffMainData(
    var subscriberTariff: TariffResponse? = null,
    var catalogTariff: CatalogTariffResponse? = null,
    var servicesList: MutableList<ServicesListShow> = mutableListOf<ServicesListShow>(),
    var indicatorHolder: MutableMap<String, IndicatorHolder>? = null,
    var indicatorModels: IndicatorsModel? = null,
    var servicesListOriginal:  List<ServicesListResponse>? = null,
    var exchangeInfo: ExchangeResponse? = null
)