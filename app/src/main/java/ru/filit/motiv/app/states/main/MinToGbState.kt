package ru.filit.motiv.app.states.main

import ru.filit.motiv.app.models.ExchangeResponse

sealed class MinToGbState {

    data class Exchanged(val status: String) : MinToGbState()

    data class EtQuantityChanged(val quantity: Int) : MinToGbState()

    data class ExchangeData(val data: ExchangeResponse) : MinToGbState()

    data class IndicatorChange(val quantity: Int) : MinToGbState()

    object ForbiddenCountMin: MinToGbState()

    object Loading: MinToGbState()

    data class InternetState(val active: Boolean): MinToGbState()
}