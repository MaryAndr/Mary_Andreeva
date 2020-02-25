package ru.filit.motiv.app.states.main

sealed class ServiceDialogState : BaseDialogState {

    data class ServiceProcessed(val isActivate: Boolean) : ServiceDialogState()

    data class ErrorShown(val error: String) : ServiceDialogState()

    object Loading : ServiceDialogState()

    data class InternetState(val active: Boolean): ServiceDialogState()
}