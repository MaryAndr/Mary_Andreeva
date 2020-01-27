package ru.filit.motiv.app.states

sealed class SendSMSPageState {

    data class ErrorState(val error: String) : SendSMSPageState()

    object SmsSend : SendSMSPageState()

    object DefaultState: SendSMSPageState()

}