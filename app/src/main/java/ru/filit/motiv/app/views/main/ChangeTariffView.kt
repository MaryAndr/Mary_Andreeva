package ru.filit.motiv.app.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.states.main.ChangeTariffState

interface ChangeTariffView : MvpView {

    fun showMainDataIntent() : Observable<Int>

    fun render(state: ChangeTariffState)
}