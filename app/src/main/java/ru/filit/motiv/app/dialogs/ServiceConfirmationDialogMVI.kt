package ru.filit.motiv.app.dialogs

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.service_confirmation_dialog.*
import kotlinx.android.synthetic.main.service_confirmation_dialog.ivClose
import kotlinx.android.synthetic.main.service_confirmation_dialog.title
import ru.filit.motiv.app.R
import ru.filit.motiv.app.adapters.ExpandableServiceAdapter
import ru.filit.motiv.app.adapters.MyTariffServicesAdapter
import ru.filit.motiv.app.adapters.ServicesViewHolder
import ru.filit.motiv.app.adapters.ViewHolder
import ru.filit.motiv.app.models.main.ServiceDialogModel
import ru.filit.motiv.app.presenters.main.ServiceDialogPresenter
import ru.filit.motiv.app.states.main.ServiceDialogState
import ru.filit.motiv.app.views.main.ServiceConfirmationDialogView


class ServiceConfirmationDialogMVI(val data: ServiceDialogModel) :
    BaseBottomDialogMVI<ServiceConfirmationDialogView, ServiceDialogState, ServiceDialogPresenter>(),
    ServiceConfirmationDialogView {

    override fun createPresenter() = ServiceDialogPresenter(context!!)

    override fun operationIntent(): Observable<ServiceDialogModel> {
        return RxView.clicks(btnProcess).flatMap {
            Observable.just(data)
        }
    }

    override fun render(state: ServiceDialogState) {
        when (state) {
            is ServiceDialogState.ServiceProcessed -> {
                val textMessage = if (state.isActivate) {
                    "Услуга «${data.serv_name}» успешно подключена"
                } else {
                    "Услуга «${data.serv_name}» успешно отключена"
                }
                val dialogBuilder = AlertDialog.Builder(this.context)
                var itemHolder = when(data.itemHolder){
                    is ExpandableServiceAdapter.ChildViewHolder -> {
                        data.itemHolder as ExpandableServiceAdapter.ChildViewHolder
                    }
                    is ViewHolder -> {
                        data.itemHolder as ViewHolder
                    }
                    else -> {
                        data.itemHolder as ServicesViewHolder
                    }
                }
                dialogBuilder
                    .setMessage(textMessage)
                    .setPositiveButton("OK") { _, _ ->
                        when(data.itemHolder){
                            is ExpandableServiceAdapter.ChildViewHolder -> {
                                (data.itemHolder as ExpandableServiceAdapter.ChildViewHolder).tgService.isEnabled = false
                            }
                            is ViewHolder -> {
                                (data.itemHolder as ViewHolder).tgButton.isEnabled = false
                            }
                            else -> {
                                (data.itemHolder as ServicesViewHolder).tgService.isEnabled = false
                            }
                        }
                    }
                    .create()
                    .show()

                dismiss()
            }
            is ServiceDialogState.ErrorShown -> {
                Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.service_confirmation_dialog, container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvServiceName.text = data.serv_name
        tvConDateValue.text = data.conDate

        tvCostValue.text = data.activationPrice + resources.getString(R.string.rub_value)
        tvAbonValue.text = data.abonPay + resources.getString(R.string.rub_value)

        ivClose.setOnClickListener {
            dismiss()
        }


        if (data.isConnection) {
            title.text = "Подключение услуги"
            tvConDateKey.text = "Дата подключения"
            btnProcess.text = "Подключить услугу"
            if (data.serv_id == "1778" || data.serv_id == "1791") {
                tvCondition.visibility = View.VISIBLE
            }
        } else {
            title.text = "Отключение услуги"
            costLayout.visibility = View.GONE
            abonLayout.visibility = View.GONE
            btnProcess.text = "Отключить услугу"
            tvConDateKey.text = "Дата отключения"
        }
    }

    companion object {

        fun newInstance(data: ServiceDialogModel): ServiceConfirmationDialogMVI {
            return ServiceConfirmationDialogMVI(data)
        }
    }

}