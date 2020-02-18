package ru.filit.motiv.app.views

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.states.SplashScreenState


interface SplashScreenView : MvpView {

    fun checkInternetConnectivityIntent(): Observable<Boolean>

    fun render(state: SplashScreenState)
}