package ru.filit.motiv.app.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.states.main.AnswerFAQState

interface AnswerFAQView: MvpView {

    fun render(state: AnswerFAQState)

    fun getAnswerIntent(): Observable<Int>

    fun checkInternetConnectivityIntent(): Observable<Boolean>
}