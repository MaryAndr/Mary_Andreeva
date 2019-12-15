package kz.atc.mobapp.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import kz.atc.mobapp.models.EmailDetalModel
import kz.atc.mobapp.states.main.CostsEmailState

interface CostsEmailView : MvpView {

    fun msisdnLoadIntent() : Observable<Int>

    fun sendEmailIntent() : Observable<EmailDetalModel>

    fun render(state: CostsEmailState)
}