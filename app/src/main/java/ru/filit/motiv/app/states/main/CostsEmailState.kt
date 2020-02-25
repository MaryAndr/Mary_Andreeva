package ru.filit.motiv.app.states.main

sealed class CostsEmailState {

    data class MsisdnShown(val defPeriod: String) : CostsEmailState()

    data class ErrorShown(val error: String) : CostsEmailState()

    object EmailSent : CostsEmailState()

    object Loading : CostsEmailState()

    data class InternetState(val active: Boolean): CostsEmailState()

}