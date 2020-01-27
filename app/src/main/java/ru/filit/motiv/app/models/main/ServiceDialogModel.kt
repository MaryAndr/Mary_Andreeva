package ru.filit.motiv.app.models.main

data class ServiceDialogModel(
    var serv_id: String? = null,
    var serv_name: String? = null,
    var activationPrice: String? = null,
    var subscriptionFee: String? = null,
    var conCost: String? = null,
    var abonPay: String? = null,
    var conDate: String? = null,
    var isConnection: Boolean = true,
    var itemHolder: Any? = null
)