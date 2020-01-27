package ru.filit.motiv.app.states

sealed class LoginPagePartialState {

    object DefaultState : LoginPagePartialState()

    object Loading : LoginPagePartialState()

    data class ErrorState(val error: String) : LoginPagePartialState()

    object Authorized : LoginPagePartialState()

}