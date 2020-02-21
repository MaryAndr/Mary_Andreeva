package ru.filit.motiv.app.states.main

import ru.filit.motiv.app.models.main.SettingsDataModel

sealed class SettingsState {

    data class MainDataLoaded(val data: SettingsDataModel) : SettingsState()

    object LogOut : SettingsState()

    object Loading : SettingsState()

    data class InternetState(val active: Boolean): SettingsState()

    data class ShowErrorMessage( val message: String): SettingsState()

}