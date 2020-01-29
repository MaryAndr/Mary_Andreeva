package ru.filit.motiv.app.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.states.main.HelpState

interface HelpView: MvpView {
    fun render(state: HelpState)
    fun getMessengersIntent(): Observable<Boolean>
}