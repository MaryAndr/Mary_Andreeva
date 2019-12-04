package kz.atc.mobapp.states.main

import kz.atc.mobapp.models.main.MainPagaAccumData

sealed class MainPagePartialState {
    
    data class ShowDataState(val data: MainPagaAccumData) : MainPagePartialState()

    data class ShowErrorMessage(val error: String) : MainPagePartialState()

    object Loading : MainPagePartialState()
}