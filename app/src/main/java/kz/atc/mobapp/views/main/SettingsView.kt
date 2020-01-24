package kz.atc.mobapp.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import kz.atc.mobapp.states.main.SettingsState

interface SettingsView : MvpView {

    fun mainDataLoadingIntent() : Observable<Int>

    fun render(state : SettingsState)

    fun logoutIntent(): Observable<Any>
}