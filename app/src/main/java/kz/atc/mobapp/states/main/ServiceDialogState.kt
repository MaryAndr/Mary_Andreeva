package kz.atc.mobapp.states.main

sealed class ServiceDialogState : BaseDialogState {

    data class ServiceProcessed(val isActivate: Boolean) : ServiceDialogState()

    data class ErrorShown(val error: String) : ServiceDialogState()

}