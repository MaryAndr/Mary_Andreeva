package ru.filit.motiv.app.dialogs

import android.content.DialogInterface
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.dialog_block_unblock.*
import ru.filit.motiv.app.R
import ru.filit.motiv.app.fragments.InternetLostFragment
import ru.filit.motiv.app.models.main.BlockUnblockDataModel
import ru.filit.motiv.app.models.main.SettingsDataModel
import ru.filit.motiv.app.presenters.main.BlockUnblockDialogPresenter
import ru.filit.motiv.app.states.main.BlockUnblockDialogState
import ru.filit.motiv.app.utils.ConnectivityReceiver
import ru.filit.motiv.app.views.main.BlockUnblockDialogView

class BlockUnblockDialog(val data: SettingsDataModel, val message: String? = null) :
    BaseBottomDialogMVI<BlockUnblockDialogView, BlockUnblockDialogState, BlockUnblockDialogPresenter>(),
    BlockUnblockDialogView{


    private lateinit var networkAvailabilityTrigger : BehaviorSubject<Boolean>

    override fun checkInternetConnectivityIntent(): Observable<Boolean> {
        return networkAvailabilityTrigger
    }

    override fun createPresenter() = BlockUnblockDialogPresenter(context!!)

    override fun processIntent(): Observable<BlockUnblockDataModel> {
        return RxView.clicks(btnBlockUnlock)
            .map<BlockUnblockDataModel> {
                if (data.statusId == 1) {
                    BlockUnblockDataModel(true, null, message)
                } else {
                    BlockUnblockDataModel(false, etKeyWord.text.toString(), null)
                }
            }
    }

    override fun render(state: BlockUnblockDialogState) {
        when (state) {
            is BlockUnblockDialogState.RequestProcessed -> {
                viewUnblock.visibility = View.VISIBLE
                no_internet_view.visibility = View.GONE
                if (state.isProcessed) {
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    targetFragment?.activity?.onBackPressed()
                    dismiss()
                } else {
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    if (state.incorrectCounter != null) {
                        if (state.incorrectCounter > 0) {
                            layoutTextInput.error =
                                "Неверное кодовое слово! У вас осталось ${state.incorrectCounter} попыток"
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (data.statusId == 1) {
            tvPhoneNumber.text = "+7${data.msisdn!!}"
            title.text = "Блокировка номера"
            viewUnblock.visibility = View.GONE
            btnBlockUnlock.text = "Блокировать номер"
        } else if (data.statusId == 4) {
            tvPhoneNumber.text = "+7${data.msisdn!!}"
            title.text = "Разблокировка номера"
            viewUnblock.visibility = View.VISIBLE
            etKeyWord.visibility = View.VISIBLE
            btnBlockUnlock.text = "Разблокировать номер"
        }
        ivClose.setOnClickListener{dismiss()}
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