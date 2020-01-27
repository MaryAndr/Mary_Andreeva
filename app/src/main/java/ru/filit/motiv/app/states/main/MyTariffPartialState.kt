package ru.filit.motiv.app.states.main

import ru.filit.motiv.app.models.main.MyTariffMainData

sealed class MyTariffPartialState {

    data class MainDataLoadedState(val data : MyTariffMainData) : MyTariffPartialState()

    data class ShowErrorMessage(val error: String) : MyTariffPartialState()

    object Loading : MyTariffPartialState()
}