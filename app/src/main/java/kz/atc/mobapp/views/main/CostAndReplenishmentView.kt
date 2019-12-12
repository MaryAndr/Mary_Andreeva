package kz.atc.mobapp.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import kz.atc.mobapp.states.main.CostAndReplenishmentState

interface CostAndReplenishmentView : MvpView {

    fun mainDataLoadIntent() : Observable<Int>

    fun showCostsIntent() : Observable<Int>

    fun showReplenishmentIntent() : Observable<Int>

    fun render(state: CostAndReplenishmentState)
}