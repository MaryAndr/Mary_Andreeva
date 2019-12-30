package kz.atc.mobapp.models.main

data class ServiceDialogModel(
    var serv_id: String? = null,
    var serv_name: String? = null,
    var conCost: String? = null,
    var abonPay: String? = null,
    var conDate: String? = null,
    var isConnection: Boolean = true,
    var itemHolder: Any? = null
)