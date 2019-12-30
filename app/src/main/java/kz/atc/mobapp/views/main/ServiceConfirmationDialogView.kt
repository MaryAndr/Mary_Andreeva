package kz.atc.mobapp.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import kz.atc.mobapp.models.main.ServiceDialogModel
import kz.atc.mobapp.states.main.ServiceDialogState

interface ServiceConfirmationDialogView : MvpView {

    fun operationIntent(): Observable<ServiceDialogModel>

    fun render(state: ServiceDialogState)
}