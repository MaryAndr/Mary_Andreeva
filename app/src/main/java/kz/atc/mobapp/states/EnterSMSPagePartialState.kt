package kz.atc.mobapp.states

sealed class EnterSMSPagePartialState {

    data class ShowTimerState(val countDown: Long) : EnterSMSPagePartialState()

    data class ErrorState(val error: String) : EnterSMSPagePartialState()

    object SmsResendedState : EnterSMSPagePartialState()

    object Authorized : EnterSMSPagePartialState()

}