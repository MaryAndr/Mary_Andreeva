package ru.filit.motiv.app.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.states.main.MyTariffState

interface MyTariffView : MvpView {

    fun checkInternetConnectivityIntent(): Observable<Boolean>

    fun preLoadIntent() : Observable<Int>

    fun changeServiceIntent(): Observable<String>

    fun cancelChangeServiceIntent (): Observable<String>

    fun render(state: MyTariffState)
}