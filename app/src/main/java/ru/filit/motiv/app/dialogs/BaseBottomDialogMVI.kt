package ru.filit.motiv.app.dialogs

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hannesdorfmann.mosby3.mvi.MviDialogFragment
import com.hannesdorfmann.mosby3.mvi.MviPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView

//Класс создан, что бы дополнить MVI диалог Mosby, это дает нам использовать bottomsheetdialog в нашей архитектуре
abstract class BaseBottomDialogMVI<V: MvpView, S:Any, P: MviPresenter<V, S>>: MviDialogFragment<V,P> (){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(this.context!!, this.theme)
    }

}