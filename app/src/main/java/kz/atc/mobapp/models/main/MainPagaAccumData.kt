package kz.atc.mobapp.models.main

import kz.atc.mobapp.models.RemainsResponse

data class MainPagaAccumData(
    var phoneNumber : String? = null,
    var tariffName: String? = null,
    var chargeDate: String? = null,
    var balance: Double? = null,
    var remains: List<RemainsResponse>? = null
)