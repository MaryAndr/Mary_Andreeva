package kz.atc.mobapp.states.main

import kz.atc.mobapp.models.main.MainPagaAccumData

data class CostAndReplenishmentState (
    var mainDataLoaded: Boolean,
    var mainData: MainPagaAccumData? = null,
    var costsShown: Boolean,
    var replenishmentShown: Boolean,
    var errorShown: Boolean,
    var errorText: String? = null
)