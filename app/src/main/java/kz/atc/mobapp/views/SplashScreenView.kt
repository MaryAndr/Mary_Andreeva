package kz.atc.mobapp.views

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import kz.atc.mobapp.states.SplashScreenState


interface SplashScreenView : MvpView {

    fun checkInternetConnectivityIntent(): Observable<Int>

    fun render(state: SplashScreenState)
}