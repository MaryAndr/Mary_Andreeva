package ru.filit.motiv.app.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.models.main.ChangePassModel
import ru.filit.motiv.app.states.main.ChangePassState

interface ChangePassView : MvpView {

    fun processIntent() : Observable<ChangePassModel>

    fun render(state: ChangePassState)

    fun checkInternetConnectivityIntent(): Observable<Boolean>

}