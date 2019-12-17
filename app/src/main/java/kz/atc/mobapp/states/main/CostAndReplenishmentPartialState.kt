package kz.atc.mobapp.states.main

import kz.atc.mobapp.models.main.MainPagaAccumData
import kz.atc.mobapp.models.main.SubPaymentsResponse

sealed class CostAndReplenishmentPartialState {

    data class ShowMainDataState(val data: MainPagaAccumData) : CostAndReplenishmentPartialState()

    data class ShowErrorState(val error: String) : CostAndReplenishmentPartialState()

    object ShowCostsLayout : CostAndReplenishmentPartialState()

    object ShowReplenishmentLayout : CostAndReplenishmentPartialState()

    object Loading : CostAndReplenishmentPartialState()

    data class ShowReplenishmentData(val payments: List<SubPaymentsResponse>) : CostAndReplenishmentPartialState()

}