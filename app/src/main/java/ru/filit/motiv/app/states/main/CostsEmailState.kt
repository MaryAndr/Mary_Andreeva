package ru.filit.motiv.app.states.main

sealed class CostsEmailState {

    data class MsisdnShown(val msisdn: String, val defPeriod: String) : CostsEmailState()

    data class ErrorShown(val error: String) : CostsEmailState()

    object EmailSent : CostsEmailState()

}