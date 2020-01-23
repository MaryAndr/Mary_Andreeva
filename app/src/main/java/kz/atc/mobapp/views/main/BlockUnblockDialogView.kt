package kz.atc.mobapp.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import kz.atc.mobapp.models.main.BlockUnblockDataModel
import kz.atc.mobapp.states.main.BlockUnblockDialogState

interface BlockUnblockDialogView : MvpView {

    fun processIntent() : Observable<BlockUnblockDataModel>

    fun render(state: BlockUnblockDialogState)
}