package ru.filit.motiv.app.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.tariff_change_dialog.*
import ru.filit.motiv.app.R
import ru.filit.motiv.app.models.main.TariffDialogModelData
import ru.filit.motiv.app.presenters.main.TariffConfirmationDialogPresenter
import ru.filit.motiv.app.states.main.TariffDialogState
import ru.filit.motiv.app.views.main.TariffConfirmationDialogView

class TariffConfirmationDialogMVI(val data: TariffDialogModelData) :
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
        if (data.tariffAbonCost != null) {
            tvAbonValue.text = data.tariffAbonCost
        } else {
            abonLayout.visibility = View.GONE
        }

        if (data.tariffChangeCost != null) {
            tvCostValue.text = data.tariffChangeCost
        } else {
            costLayout.visibility = View.GONE
        }

    }

    override fun render(state: TariffDialogState) {
        when(state) {
            is TariffDialogState.TariffProcessed -> {
                Toast.makeText(context, state.toastText, Toast.LENGTH_LONG).show()
                dismiss()
            }
            is TariffDialogState.ErrorShown -> {
                Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
                dismiss()
            }
        }
    }


    companion object {

        fun newInstance(data: TariffDialogModelData): TariffConfirmationDialogMVI {
            return TariffConfirmationDialogMVI(data)
        }
    }
}