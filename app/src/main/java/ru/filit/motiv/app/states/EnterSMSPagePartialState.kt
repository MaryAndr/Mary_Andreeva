package ru.filit.motiv.app.states

sealed class EnterSMSPagePartialState {

    data class ShowTimerState(val countDown: Long) : EnterSMSPagePartialState()

    data class ErrorState(val error: String) : EnterSMSPagePartialState()

    object SmsResendedState : EnterSMSPagePartialState()

    object BlankState : EnterSMSPagePartialState()

    object Authorized : EnterSMSPagePartialState()

}