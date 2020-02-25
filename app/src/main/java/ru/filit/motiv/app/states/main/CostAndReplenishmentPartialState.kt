package ru.filit.motiv.app.states.main

import ru.filit.motiv.app.models.main.MainPagaAccumData
import ru.filit.motiv.app.models.main.SubPaymentsResponse

sealed class CostAndReplenishmentPartialState {

    data class ShowMainDataState(val data: MainPagaAccumData) : CostAndReplenishmentPartialState()

    data class ShowErrorState(val error: String) : CostAndReplenishmentPartialState()

    object ShowCostsLayout : CostAndReplenishmentPartialState()

    object ShowReplenishmentLayout : CostAndReplenishmentPartialState()

    object Loading : CostAndReplenishmentPartialState()

    data class ShowReplenishmentData(val payments: List<SubPaymentsResponse>) : CostAndReplenishmentPartialState()

    data class InternetState(val active: Boolean): CostAndReplenishmentPartialState()

}