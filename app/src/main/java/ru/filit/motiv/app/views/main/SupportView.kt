package ru.filit.motiv.app.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.states.main.SupportState

interface SupportView: MvpView {
    fun render(state: SupportState)
    fun getMessengersIntent(): Observable<Boolean>
}