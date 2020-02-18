package ru.filit.motiv.app.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.states.main.SettingsState

interface SettingsView : MvpView {

    fun mainDataLoadingIntent() : Observable<Int>

    fun render(state : SettingsState)

    fun logoutIntent(): Observable<Any>

    fun checkInternetConnectivityIntent(): Observable<Boolean>
}