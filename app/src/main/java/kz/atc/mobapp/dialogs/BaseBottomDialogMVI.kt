package kz.atc.mobapp.dialogs

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hannesdorfmann.mosby3.mvi.MviDialogFragment
import com.hannesdorfmann.mosby3.mvi.MviPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import kz.atc.mobapp.states.main.ServiceDialogState

//Created to extend MVI version of Mosby to support Bottom Sheet Dialog
abstract class BaseBottomDialogMVI<V: MvpView, P: MviPresenter<V, ServiceDialogState>>: MviDialogFragment<V,P> (){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(this.context!!, this.theme)
    }

}