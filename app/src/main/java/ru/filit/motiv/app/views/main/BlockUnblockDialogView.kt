package ru.filit.motiv.app.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.models.main.BlockUnblockDataModel
import ru.filit.motiv.app.states.main.BlockUnblockDialogState

interface BlockUnblockDialogView : MvpView {

    fun processIntent() : Observable<BlockUnblockDataModel>

    fun render(state: BlockUnblockDialogState)

    fun checkInternetConnectivityIntent(): Observable<Boolean>
}