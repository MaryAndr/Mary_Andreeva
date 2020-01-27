package ru.filit.motiv.app.presenters.main

import android.content.Context
import com.google.gson.Gson
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.filit.motiv.app.models.ErrorJson
import ru.filit.motiv.app.models.main.ExchangeRequest
import ru.filit.motiv.app.presenters.interactors.SubscriberInteractor
import ru.filit.motiv.app.states.main.MinToGbState
import ru.filit.motiv.app.views.main.MinToGbView
import retrofit2.HttpException

class MinToGbPresenter(val ctx: Context) :
    MviBasePresenter<MinToGbView, MinToGbState>() {

    private val gson = Gson()

    override fun bindIntents() {
        val changeExchangeIntent: Observable<MinToGbState> =
            intent(MinToGbView::changeQuantityIntent).flatMap {
                Observable.just(MinToGbState.EtQuantityChanged(it))
            }


        val changeIndicatorIntent: Observable<MinToGbState> =
            intent(MinToGbView::changeIndicatorIntent).flatMap {
                Observable.just(MinToGbState.IndicatorChange(it))
            }


        val exchangeIntent: Observable<MinToGbState> =
            intent(MinToGbView::exchangeMinsIntent).flatMap {
                SubscriberInteractor(ctx).subService.exchangeMins(ExchangeRequest(it))
                    .flatMap {
                        Observable.just(MinToGbState.Exchanged("Обмен успешно произведен"))
                    }.subscribeOn(Schedulers.io()).onErrorReturn { error: Throwable ->
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
                        MinToGbState.Exchanged(errMessage)
                    }
            }

        val fetchData: Observable<MinToGbState> =
            intent(MinToGbView::getExchangeDataIntent).flatMap {
                SubscriberInteractor(ctx).subService.getExchangeInfo().flatMap {
                    Observable.just(MinToGbState.ExchangeData(it))
                }.subscribeOn(Schedulers.io())
            }

        val allIntents =
            Observable.merge(changeExchangeIntent, fetchData, exchangeIntent, changeIndicatorIntent)
                .observeOn(AndroidSchedulers.mainThread())


        subscribeViewState(allIntents, MinToGbView::render)
    }

}