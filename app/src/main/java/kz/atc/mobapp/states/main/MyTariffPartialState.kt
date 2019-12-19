package kz.atc.mobapp.states.main

import kz.atc.mobapp.models.main.MyTariffMainData

sealed class MyTariffPartialState {

    data class MainDataLoadedState(val data : MyTariffMainData) : MyTariffPartialState()

    data class ShowErrorMessage(val error: String) : MyTariffPartialState()
}