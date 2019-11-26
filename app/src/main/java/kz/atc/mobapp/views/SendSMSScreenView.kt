package kz.atc.mobapp.views

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import kz.atc.mobapp.states.SendSMSPageState

interface SendSMSScreenView : MvpView {

    fun sendSMSButtonIntent() : Observable<String>

    fun defaultIntent(): Observable<Int>

    fun render(state: SendSMSPageState)
}