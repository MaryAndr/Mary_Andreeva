package kz.atc.mobapp.states.main

sealed class ServiceDialogState {

    data class ServiceProcessed(val isActivate: Boolean) : ServiceDialogState()

    data class ErrorShown(val error: String) : ServiceDialogState()

}