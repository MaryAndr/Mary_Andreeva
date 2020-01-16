package kz.atc.mobapp.states.main

import kz.atc.mobapp.models.main.TariffShow

sealed class ChangeTariffState : BaseDialogState {

    data class MainDataLoaded(val data: MutableList<TariffShow>) : ChangeTariffState()

}