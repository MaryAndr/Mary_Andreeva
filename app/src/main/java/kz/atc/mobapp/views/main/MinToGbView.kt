package kz.atc.mobapp.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import kz.atc.mobapp.models.ExchangeResponse
import kz.atc.mobapp.states.main.MinToGbState

interface MinToGbView : MvpView {

    fun changeQuantityIntent() : Observable<Int>

    fun getExchangeDataIntent() : Observable<Int>

    fun exchangeMinsIntent(): Observable<Int>

    fun changeIndicatorIntent() : Observable<Int>

    fun render(state: MinToGbState)
}