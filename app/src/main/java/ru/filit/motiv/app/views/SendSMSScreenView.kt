package ru.filit.motiv.app.views

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.states.SendSMSPageState

interface SendSMSScreenView : MvpView {

    fun sendSMSButtonIntent() : Observable<String>

    fun defaultIntent(): Observable<Int>

    fun render(state: SendSMSPageState)
}