package ru.filit.motiv.app.dialogs

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.dialog_block_unblock.*
import ru.filit.motiv.app.R
import ru.filit.motiv.app.fragments.InternetLostFragment
import ru.filit.motiv.app.models.main.BlockUnblockDataModel
import ru.filit.motiv.app.models.main.SettingsDataModel
import ru.filit.motiv.app.presenters.main.BlockUnblockDialogPresenter
import ru.filit.motiv.app.states.main.BlockUnblockDialogState
import ru.filit.motiv.app.utils.TextConverter
import ru.filit.motiv.app.utils.hideKeyboard
import ru.filit.motiv.app.views.main.BlockUnblockDialogView

class BlockUnblockDialog(val data: SettingsDataModel, val message: String? = null) :
    BaseBottomDialogMVI<BlockUnblockDialogView, BlockUnblockDialogState, BlockUnblockDialogPresenter>(),
    BlockUnblockDialogView {


    private lateinit var networkAvailabilityTrigger : BehaviorSubject<Boolean>

    private lateinit var processIntentTrigger: BehaviorSubject<BlockUnblockDataModel>

    override fun checkInternetConnectivityIntent(): Observable<Boolean> {
        return networkAvailabilityTrigger
    }

    override fun createPresenter() = BlockUnblockDialogPresenter(context!!)

    override fun processIntent(): Observable<BlockUnblockDataModel> {
        return processIntentTrigger
    }

    override fun render(state: BlockUnblockDialogState) {
        when (state) {
            is BlockUnblockDialogState.RequestProcessed -> {
                viewUnblock.visibility = View.VISIBLE
                no_internet_view.visibility = View.GONE
                if (state.isProcessed) {
                    val dialogBuilder = AlertDialog.Builder(this.context)
                    dialogBuilder
                        .setMessage(state.message)
                        .setPositiveButton("OK") { _, _ ->
                        }
                        .create()
                        .show()
                    targetFragment?.activity?.onBackPressed()
                    dismiss()
                } else {
                    val dialogBuilder = AlertDialog.Builder(this.context)
                    dialogBuilder
                        .setMessage(state.message)
                        .setPositiveButton("OK") { _, _ ->
                        }
                        .create()
                        .show()
                    if (state.incorrectCounter != null) {
                        if (state.incorrectCounter > 0) {
                            layoutTextInput.error =
                                "Неверное кодовое слово! У вас осталось ${state.incorrectCounter} ${TextConverter().getDeclensionOfAttempt(state.incorrectCounter)}"
                        } else {
                            layoutTextInput.visibility = View.GONE
                            tvWrongKeyTitle.visibility = View.VISIBLE
                            tvWrongKeySubTitle.visibility = View.VISIBLE
                        }
                    }
                }
            }
            is BlockUnblockDialogState.InternetState -> {
                val fragment = InternetLostFragment()
                activity!!.supportFragmentManager.beginTransaction()
                    .addToBackStack("internetlost")
                    .replace(R.id.container, fragment)
                    .commit()
                dismiss()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_block_unblock, container,
            false
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkAvailabilityTrigger = BehaviorSubject.create()
        processIntentTrigger = BehaviorSubject.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (data.statusId == 1) {
            tvPhoneNumber.text = "+7${data.msisdn}"
            title.text = "Блокировка номера"
            viewUnblock.visibility = View.GONE
            btnBlockUnlock.text = "Блокировать номер"
        } else if (data.statusId == 4) {
            tvPhoneNumber.text = "+7${data.msisdn}"
            title.text = "Разблокировка номера"
            viewUnblock.visibility = View.VISIBLE
            etKeyWord.visibility = View.VISIBLE
            btnBlockUnlock.text = "Разблокировать номер"
        }
        ivClose.setOnClickListener{dismiss()}
        btnBlockUnlock.setOnClickListener{
            if (data.statusId == 1) {
                processIntentTrigger.onNext(BlockUnblockDataModel(true, null, message))
            } else {
                processIntentTrigger.onNext(BlockUnblockDataModel(false, etKeyWord.text.toString(), null))
            }
        }

        etKeyWord.setOnEditorActionListener{v: TextView?, actionId: Int?, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                processIntentTrigger.onNext(BlockUnblockDataModel(false, v?.text.toString(), null))
                hideKeyboard()
                 true
            }else false
        }
    }




    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    companion object {

        fun newInstance(data: SettingsDataModel, message: String? = null): BlockUnblockDialog {
            return BlockUnblockDialog(data, message)
        }
    }
}