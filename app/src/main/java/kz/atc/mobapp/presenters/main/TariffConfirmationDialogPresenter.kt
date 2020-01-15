package kz.atc.mobapp.presenters.main

import android.content.Context
import com.google.gson.Gson
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.models.ErrorJson
import kz.atc.mobapp.models.main.TariffChangeRequest
import kz.atc.mobapp.presenters.interactors.SubscriberInteractor
import kz.atc.mobapp.states.main.TariffDialogState
import kz.atc.mobapp.views.main.ServiceConfirmationDialogView
import kz.atc.mobapp.views.main.TariffConfirmationDialogView
import retrofit2.HttpException

class TariffConfirmationDialogPresenter(val context: Context) :
    MviBasePresenter<TariffConfirmationDialogView, TariffDialogState>() {

    private val gson = Gson()

    override fun bindIntents() {
        val operationIntent: Observable<TariffDialogState> =
            intent(TariffConfirmationDialogView::operationIntent)
                .flatMap { model ->
                    SubscriberInteractor(context).subService.changeTariff(TariffChangeRequest(model))
                        .map<TariffDialogState> {
                            TariffDialogState.TariffProcessed(true)
                        }
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn { error: Throwable ->
                            var errMessage = error.localizedMessage
                            if (error is HttpException) {
                                errMessage = if (error.code() == 409) {
                                    "Вы не являетесь пользователем мобильной связи"
                                } else {
                                    val errorBody = error.response()!!.errorBody()

                                    val adapter =
                                        gson.getAdapter<ErrorJson>(ErrorJson::class.java!!)
                                    val errorObj = adapter.fromJson(errorBody!!.string())
                                    errorObj.error_description
                                }
                            }
                            TariffDialogState.ErrorShown(errMessage)
                        }
                }

        val allIntents = operationIntent
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(allIntents, TariffConfirmationDialogView::render)
    }


}