package kz.atc.mobapp.states

sealed class SplashScreenState {

    object LoadingState : SplashScreenState()

    data class InternetState(val active: Boolean) : SplashScreenState()
}