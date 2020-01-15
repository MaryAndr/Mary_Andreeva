package kz.atc.mobapp.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import kz.atc.mobapp.states.main.TariffDialogState

interface TariffConfirmationDialogView: MvpView {

    fun operationIntent(): Observable<String>

    fun render(state: TariffDialogState)
}