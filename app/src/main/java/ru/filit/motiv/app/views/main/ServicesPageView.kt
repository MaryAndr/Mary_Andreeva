package ru.filit.motiv.app.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.states.main.ServicesState

interface ServicesPageView : MvpView {

    fun showEnabledServiceIntent() : Observable<Boolean>

    fun render(state: ServicesState)
}