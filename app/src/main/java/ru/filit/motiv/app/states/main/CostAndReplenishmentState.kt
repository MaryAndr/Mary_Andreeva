package ru.filit.motiv.app.states.main

import ru.filit.motiv.app.models.main.MainPagaAccumData
import ru.filit.motiv.app.models.main.SubPaymentsResponse

data class CostAndReplenishmentState (
    var mainDataLoaded: Boolean,
    var mainData: MainPagaAccumData? = null,
    var costsShown: Boolean,
    var replenishmentShown: Boolean,
    var errorShown: Boolean,
    var errorText: String? = null,
    var replenishmentDataLoaded: Boolean,
    var replenishmentData: List<SubPaymentsResponse>? = null,
    var loading: Boolean,
    var connectionLost: Boolean,
    var connectionResume: Boolean
)