package kz.atc.mobapp.states.main

sealed class CostsEmailState {

    data class MsisdnShown(val msisdn: String) : CostsEmailState()

    data class ErrorShown(val error: String) : CostsEmailState()

    object EmailSent : CostsEmailState()

}