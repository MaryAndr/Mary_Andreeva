package kz.atc.mobapp.states.main

sealed class TariffDialogState {

    data class TariffProcessed(val toastText: String) : TariffDialogState()

    data class ErrorShown(val error: String) : TariffDialogState()

}