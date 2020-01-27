package ru.filit.motiv.app.models.main

data class TariffShow(
    var id: String? = null,
    var isCurrent: Boolean = false,
    var name: String? = null,
    var description: String? = null,
    var category: String? = null,
    var dataValueUnit: String? = null,
    var voiceValueUnit: String? = null,
    var smsValueUnit: String? = null,
    var price: String? = null,
    var aboutData: MyTariffAboutData? = null
)