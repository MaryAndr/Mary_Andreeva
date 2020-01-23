package kz.atc.mobapp.dialogs

import io.reactivex.Observable
import kz.atc.mobapp.models.main.BlockUnblockDataModel
import kz.atc.mobapp.presenters.main.BlockUnblockPresenter
import kz.atc.mobapp.states.main.BlockUnblockDialogState
import kz.atc.mobapp.views.main.BlockUnblockDialogView

class BlockUnblockDialog :
    BaseBottomDialogMVI<BlockUnblockDialogView, BlockUnblockDialogState, BlockUnblockPresenter>(),
    BlockUnblockDialogView {
    override fun createPresenter(): BlockUnblockPresenter {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun processIntent(): Observable<BlockUnblockDataModel> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun render(state: BlockUnblockDialogState) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}