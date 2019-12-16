package kz.atc.mobapp.states.main

import kz.atc.mobapp.models.main.MainPagaAccumData
import kz.atc.mobapp.models.main.SubPaymentsResponse

sealed class CostAndReplenishmentPartialState {

    data class ShowMainDataState(val data: MainPagaAccumData) : CostAndReplenishmentPartialState()

    object ShowCostsLayout : CostAndReplenishmentPartialState()

    object ShowReplenishmentLayout : CostAndReplenishmentPartialState()

    data class ShowReplenishmentData(val payments: List<SubPaymentsResponse>) : CostAndReplenishmentPartialState()

}