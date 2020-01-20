package kz.atc.mobapp.presenters.main

import android.content.Context
import com.google.gson.Gson
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.models.ErrorJson
import kz.atc.mobapp.models.main.ExchangeRequest
import kz.atc.mobapp.presenters.interactors.SubscriberInteractor
import kz.atc.mobapp.states.main.MinToGbState
import kz.atc.mobapp.views.main.MinToGbView
import retrofit2.HttpException

class MinToGbPresenter(val ctx: Context) :
    MviBasePresenter<MinToGbView, MinToGbState>() {

    private val gson = Gson()

    override fun bindIntents() {
        val changeExchangeIntent: Observable<MinToGbState> =
            intent(MinToGbView::changeQuantityIntent).flatMap {
                Observable.just(MinToGbState.EtQuantityChanged(it))
            }


        val exchangeInten: Observable<MinToGbState> =
            intent(MinToGbView::exchangeMinsIntent).flatMap {
                SubscriberInteractor(ctx).subService.exchangeMins(ExchangeRequest(it))
                    .flatMap {
                        Observable.just(MinToGbState.Exchanged("Success"))
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

        val allIntents = Observable.merge(changeExchangeIntent,fetchData, exchangeInten)
            .observeOn(AndroidSchedulers.mainThread())


        subscribeViewState(allIntents, MinToGbView::render)
    }

}