package kz.atc.mobapp.states.main

import kz.atc.mobapp.models.ExchangeResponse

sealed class MinToGbState {

    data class Exchanged(val status: String) : MinToGbState()

    data class EtQuantityChanged(val quantity: Int) : MinToGbState()

    data class ExchangeData(val data: ExchangeResponse) : MinToGbState()

    data class IndicatorChange(val quantity: Int) : MinToGbState()

}