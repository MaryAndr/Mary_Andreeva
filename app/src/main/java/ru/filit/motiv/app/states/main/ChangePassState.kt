package ru.filit.motiv.app.states.main

sealed class ChangePassState {

    data class Processed(val result: String) : ChangePassState()

    data class ValidationError(val errNewPass: String? = null, val errCurrPass: String? = null) : ChangePassState()

    data class InternetState(val active: Boolean): ChangePassState()

    object Loading : ChangePassState()

}