package ru.filit.motiv.app.presenters.main

import android.content.Context
import com.google.gson.Gson
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.filit.motiv.app.models.ErrorJson
import ru.filit.motiv.app.models.main.TariffChangeRequest
import ru.filit.motiv.app.presenters.interactors.SubscriberInteractor
import ru.filit.motiv.app.states.main.TariffDialogState
import ru.filit.motiv.app.views.main.TariffConfirmationDialogView
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

class TariffConfirmationDialogPresenter(val context: Context) :
    MviBasePresenter<TariffConfirmationDialogView, TariffDialogState>() {

    private val gson = Gson()

    override fun bindIntents() {
        val operationIntent: Observable<TariffDialogState> =
            intent(TariffConfirmationDialogView::operationIntent)
                .flatMap { model ->
                    SubscriberInteractor(context).subService.changeTariff(TariffChangeRequest(model))
                        .map<TariffDialogState> {
                            TariffDialogState.Loading
                        }
                        .concatWith(delayTimer())
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
                        .startWith(TariffDialogState.Loading)
                }

        val allIntents = operationIntent
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(allIntents, TariffConfirmationDialogView::render)
    }


    private fun delayTimer(): Observable<TariffDialogState> {
        val start: Long = 10
        return Observable.interval(0, 1, TimeUnit.SECONDS)
            .map { i -> start - i }
            .take(start + 1)
            .flatMap<TariffDialogState> {
                if (it.toInt() == 0) {
                    Observable.just(TariffDialogState.TariffProcessed("Тариф успешно подключен"))
                } else {
                    Observable.just(TariffDialogState.Loading)
                }
            }.subscribeOn(Schedulers.io())
    }

}