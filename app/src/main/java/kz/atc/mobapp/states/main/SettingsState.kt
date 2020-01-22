package kz.atc.mobapp.states.main

import kz.atc.mobapp.models.main.SettingsDataModel

sealed class SettingsState {

    data class MainDataLoaded(val data: SettingsDataModel) : SettingsState()

    object Loading : SettingsState()

}