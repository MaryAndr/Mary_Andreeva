package kz.atc.mobapp.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import kz.atc.mobapp.states.main.ChangeTariffState

interface ChangeTariffView : MvpView {

    fun showMainDataIntent() : Observable<Int>

    fun render(state: ChangeTariffState)
}