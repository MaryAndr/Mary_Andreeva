package kz.atc.mobapp.models.main

import android.content.Context
import kz.atc.mobapp.models.catalogTariff.CatalogTariffResponse
import kz.atc.mobapp.models.TariffResponse

data class MyTariffAboutData(
    val subscriberTariff: TariffResponse? = null,
    val catalogTariff: CatalogTariffResponse? = null,
    val subscriberServices: List<ServicesListResponse>? = null
)