package kz.atc.mobapp.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import kz.atc.mobapp.states.main.MyTariffState

interface MyTariffView : MvpView {

    fun preLoadIntent() : Observable<Int>

    fun render(state: MyTariffState)
}