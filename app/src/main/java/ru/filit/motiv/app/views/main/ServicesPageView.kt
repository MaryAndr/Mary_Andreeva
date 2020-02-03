package ru.filit.motiv.app.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.models.main.ServiceDialogModel
import ru.filit.motiv.app.states.main.ServicesPartialState
import ru.filit.motiv.app.states.main.ServicesState

interface ServicesPageView : MvpView {

    fun showEnabledServiceIntent() : Observable<Boolean>

    fun changeServiceIntent(): Observable<String>

    fun render(state: ServicesPartialState)

    fun cancelChangeServiceIntent (): Observable<Boolean>
}