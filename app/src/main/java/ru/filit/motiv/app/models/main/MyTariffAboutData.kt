package ru.filit.motiv.app.models.main

import ru.filit.motiv.app.models.catalogTariff.CatalogTariffResponse
import ru.filit.motiv.app.models.TariffResponse

data class MyTariffAboutData(
    val subscriberTariff: TariffResponse? = null,
    val catalogTariff: CatalogTariffResponse? = null,
    val subscriberServices: List<ServicesListResponse>? = null
)