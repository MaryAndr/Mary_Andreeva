package ru.filit.motiv.app.dialogs

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.service_confirmation_dialog.*
import kotlinx.android.synthetic.main.tariff_change_dialog.*
import kotlinx.android.synthetic.main.tariff_change_dialog.abonLayout
import kotlinx.android.synthetic.main.tariff_change_dialog.btnProcess
import kotlinx.android.synthetic.main.tariff_change_dialog.costLayout
import kotlinx.android.synthetic.main.tariff_change_dialog.ivClose
import kotlinx.android.synthetic.main.tariff_change_dialog.tvAbonValue
import kotlinx.android.synthetic.main.tariff_change_dialog.tvCostValue
import kotlinx.android.synthetic.main.tariff_change_dialog.tvServiceName
import ru.filit.motiv.app.R
import ru.filit.motiv.app.models.main.TariffDialogModelData
import ru.filit.motiv.app.presenters.main.TariffConfirmationDialogPresenter
import ru.filit.motiv.app.states.main.TariffDialogState
import ru.filit.motiv.app.views.main.TariffConfirmationDialogView

class TariffConfirmationDialogMVI(val data: TariffDialogModelData, private val reloadTrigger: BehaviorSubject<Int>? = null, val parentDialog: MyTariffAboutDialog? = null) :
    BaseBottomDialogMVI<TariffConfirmationDialogView, TariffDialogState, TariffConfirmationDialogPresenter>(),
    TariffConfirmationDialogView {
    override fun createPresenter() = TariffConfirmationDialogPresenter(context!!)

    override fun operationIntent(): Observable<String> {
        return RxView.clicks(btnProcess)
            .map<String> {
                data.tariffId
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v =  inflater.inflate(
            R.layout.tariff_change_dialog, container,
            false
        )
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvServiceName.text = data.tariffName

        ivClose.setOnClickListener {
            dismiss()
        }

        if (data.tariffAbonCost != null) {
            tvAbonValue.text = data.tariffAbonCost + resources.getString(R.string.rub_value)
        } else {
            abonLayout.visibility = View.GONE
        }

        if (data.tariffChangeCost != null) {
            tvCostValue.text = data.tariffChangeCost + resources.getString(R.string.rub_value)
        } else {
            costLayout.visibility = View.GONE
        }

    }

    override fun render(state: TariffDialogState) {
        when(state) {
            is TariffDialogState.Loading -> {
                btnProcess.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            }
            is TariffDialogState.TariffProcessed -> {
                btnProcess.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                val dialogBuilder = AlertDialog.Builder(this.context)
                dialogBuilder
                    .setMessage(state.toastText)
                    .setPositiveButton("OK") { _, _ ->
                    }
                    .create()
                    .show()
                reloadTrigger!!.onNext(1)
                dismiss()
                parentDialog?.dismiss()
            }
            is TariffDialogState.ErrorShown -> {
                btnProcess.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
                dismiss()
            }
        }
    }


    companion object {

        fun newInstance(data: TariffDialogModelData, reloadTrigger: BehaviorSubject<Int>? = null, parentDialog: MyTariffAboutDialog? = null): TariffConfirmationDialogMVI {
            return TariffConfirmationDialogMVI(data, reloadTrigger, parentDialog)
        }
    }
}