package ru.filit.motiv.app.states.main

import ru.filit.motiv.app.models.main.MainPagaAccumData

sealed class MainPagePartialState {
    
    data class ShowDataState(val data: MainPagaAccumData) : MainPagePartialState()

    data class ShowErrorMessage(val error: String?) : MainPagePartialState()

    object Loading : MainPagePartialState()
}