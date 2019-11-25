package kz.atc.mobapp.states

sealed class LoginPagePartialState {

    object DefaultState : LoginPagePartialState()

    object Loading : LoginPagePartialState()

    data class ErrorState(val error: String) : LoginPagePartialState()

    object Authorized : LoginPagePartialState()

}