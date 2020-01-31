package ru.filit.motiv.app.states.main

import ru.filit.motiv.app.models.main.TariffShow

sealed class ChangeTariffState : BaseDialogState {

    data class MainDataLoaded(val data: MutableList<TariffShow>) : ChangeTariffState()

    object Loading : ChangeTariffState()

}