package ru.filit.motiv.app.states.main

sealed class TariffDialogState {

    data class TariffProcessed(val toastText: String) : TariffDialogState()

    data class ErrorShown(val error: String) : TariffDialogState()

    object Loading : TariffDialogState()

    data class InternetState(val active: Boolean): TariffDialogState()
}