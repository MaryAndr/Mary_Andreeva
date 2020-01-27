package ru.filit.motiv.app.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.models.EmailDetalModel
import ru.filit.motiv.app.states.main.CostsEmailState

interface CostsEmailView : MvpView {

    fun msisdnLoadIntent() : Observable<Int>

    fun sendEmailIntent() : Observable<EmailDetalModel>

    fun render(state: CostsEmailState)
}