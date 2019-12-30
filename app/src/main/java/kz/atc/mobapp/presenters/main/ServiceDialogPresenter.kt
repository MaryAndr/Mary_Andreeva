package kz.atc.mobapp.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.presenters.interactors.SubscriberInteractor
import kz.atc.mobapp.states.main.ServiceDialogState
import kz.atc.mobapp.views.main.ServiceConfirmationDialogView

class ServiceDialogPresenter(val context: Context) :
    MviBasePresenter<ServiceConfirmationDialogView, ServiceDialogState>() {

    override fun bindIntents() {
        val operationIntent: Observable<ServiceDialogState> =
            intent(ServiceConfirmationDialogView::operationIntent)
                .flatMap { model ->
                    if (!model.isConnection) {
                        SubscriberInteractor(context).subService.deleteService(model.serv_id)
                            .flatMap {
                                if (it.status == "Услуга успешно отключена") {
                                    Observable.just(ServiceDialogState.ServiceProcessed(model.isConnection))
                                } else {
                                    Observable.just(ServiceDialogState.ErrorShown(it.status))
                                }
                            }.subscribeOn(Schedulers.io())
                    } else {
                        SubscriberInteractor(context).subService.activateService(model.serv_id)
                            .flatMap {
                                if (it.status == "Услуга успешно подключена") {
                                    Observable.just(ServiceDialogState.ServiceProcessed(model.isConnection))
                                } else {
                                    Observable.just(ServiceDialogState.ErrorShown(it.status))
                                }
                            }.subscribeOn(Schedulers.io())
                    }
                }

        val allIntents = operationIntent
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(allIntents, ServiceConfirmationDialogView::render)
    }

}