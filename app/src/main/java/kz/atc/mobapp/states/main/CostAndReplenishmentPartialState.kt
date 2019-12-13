package kz.atc.mobapp.states.main

import kz.atc.mobapp.models.main.MainPagaAccumData

sealed class CostAndReplenishmentPartialState {

    data class ShowMainDataState(val data: MainPagaAccumData) : CostAndReplenishmentPartialState()

    object ShowCostsLayout : CostAndReplenishmentPartialState()

    object ShowReplenishmentLayout : CostAndReplenishmentPartialState()

}