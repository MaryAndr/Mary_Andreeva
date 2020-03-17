package ru.filit.motiv.app.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.states.main.FAQState

interface FAQView: MvpView {

    fun render(state: FAQState)

    fun getFAQIntent(): Observable<Int>

    fun checkInternetConnectivityIntent(): Observable<Boolean>
}