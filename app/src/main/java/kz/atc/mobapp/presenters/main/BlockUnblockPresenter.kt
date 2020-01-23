package kz.atc.mobapp.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import kz.atc.mobapp.states.main.BlockUnblockDialogState
import kz.atc.mobapp.views.main.BlockUnblockDialogView

class BlockUnblockPresenter (val ctx:Context) :
    MviBasePresenter<BlockUnblockDialogView, BlockUnblockDialogState>() {

    override fun bindIntents() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}