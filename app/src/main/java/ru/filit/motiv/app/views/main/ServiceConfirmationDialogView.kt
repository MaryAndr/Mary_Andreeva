package ru.filit.motiv.app.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.models.main.ServiceDialogModel
import ru.filit.motiv.app.states.main.ServiceDialogState

interface ServiceConfirmationDialogView : MvpView {

    fun operationIntent(): Observable<ServiceDialogModel>

    fun render(state: ServiceDialogState)
}