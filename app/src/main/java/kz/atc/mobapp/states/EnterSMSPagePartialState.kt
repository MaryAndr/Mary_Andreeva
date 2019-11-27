package kz.atc.mobapp.states

sealed class EnterSMSPagePartialState {

    data class ShowTimerState(val countDown: Long) : EnterSMSPagePartialState()

    data class ErrorState(val error: String) : EnterSMSPagePartialState()

    object SmsResendedState : EnterSMSPagePartialState()

    object BlankState : EnterSMSPagePartialState()

    object Authorized : EnterSMSPagePartialState()

}