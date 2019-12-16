package kz.atc.mobapp.states.main

import kz.atc.mobapp.models.main.MainPagaAccumData
import kz.atc.mobapp.models.main.SubPaymentsResponse

data class CostAndReplenishmentState (
    var mainDataLoaded: Boolean,
    var mainData: MainPagaAccumData? = null,
    var costsShown: Boolean,
    var replenishmentShown: Boolean,
    var errorShown: Boolean,
    var errorText: String? = null,
    var replenishmentDataLoaded: Boolean,
    var replenishmentData: List<SubPaymentsResponse>? = null
)