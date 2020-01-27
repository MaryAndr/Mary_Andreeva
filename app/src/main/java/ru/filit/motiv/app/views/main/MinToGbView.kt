package ru.filit.motiv.app.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.states.main.MinToGbState

interface MinToGbView : MvpView {

    fun changeQuantityIntent() : Observable<Int>

    fun getExchangeDataIntent() : Observable<Int>

    fun exchangeMinsIntent(): Observable<Int>

    fun changeIndicatorIntent() : Observable<Int>

    fun render(state: MinToGbState)
}