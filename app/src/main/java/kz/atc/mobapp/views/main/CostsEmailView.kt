package kz.atc.mobapp.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import kz.atc.mobapp.states.main.CostsEmailState

interface CostsEmailView : MvpView {

    fun msisdnLoadIntent() : Observable<Int>

    fun sendEmailIntent() : Observable<String>

    fun render(state: CostsEmailState)
}