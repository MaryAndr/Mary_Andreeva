package kz.atc.mobapp.states.main

import kz.atc.mobapp.models.main.MainPagaAccumData

class CostAndReplenishmentState (
    var mainDataLoaded: Boolean,
    var mainData: MainPagaAccumData? = null,
    var errorShown: Boolean,
    var errorText: String? = null
)