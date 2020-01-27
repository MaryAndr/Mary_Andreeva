package ru.filit.motiv.app.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.states.main.CostAndReplenishmentState

interface CostAndReplenishmentView : MvpView {

    fun mainDataLoadIntent() : Observable<Int>

    fun showCostsIntent() : Observable<Int>

    fun showReplenishmentIntent() : Observable<Int>

    fun getReplenishmentDataIntent(): Observable<String>

    fun render(state: CostAndReplenishmentState)
}