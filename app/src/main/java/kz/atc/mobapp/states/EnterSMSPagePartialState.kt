package kz.atc.mobapp.states

sealed class EnterSMSPagePartialState {

    data class ShowTimerState(val countDown: Long) : EnterSMSPagePartialState()

    data class ErrorState(val error: String) : EnterSMSPagePartialState()

    object ShowResendTv : EnterSMSPagePartialState()

    object Authorized : EnterSMSPagePartialState()

}