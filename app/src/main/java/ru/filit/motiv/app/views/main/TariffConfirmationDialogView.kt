package ru.filit.motiv.app.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.states.main.TariffDialogState

interface TariffConfirmationDialogView: MvpView {

    fun operationIntent(): Observable<String>

    fun render(state: TariffDialogState)
}