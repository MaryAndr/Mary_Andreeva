package kz.atc.mobapp.models.main

import kz.atc.mobapp.models.TariffResponse
import kz.atc.mobapp.models.catalogTariff.CatalogTariffResponse

data class MyTariffMainData(
    var subscriberTariff: TariffResponse? = null,
    var catalogTariff: CatalogTariffResponse? = null,
    var servicesList: MutableList<ServicesListShow> = mutableListOf<ServicesListShow>(),
    var indicatorHolder: MutableMap<String, IndicatorHolder>? = null,
    var indicatorModels: IndicatorsModel? = null,
    var servicesListOriginal:  List<ServicesListResponse>? = null
)