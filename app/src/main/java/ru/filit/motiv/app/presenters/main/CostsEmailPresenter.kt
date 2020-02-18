package ru.filit.motiv.app.presenters.main

import android.content.Context
import com.google.gson.Gson
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.filit.motiv.app.models.ErrorJson
import ru.filit.motiv.app.presenters.interactors.SubscriberInteractor
import ru.filit.motiv.app.states.main.CostsEmailState
import ru.filit.motiv.app.views.main.CostsEmailView
import retrofit2.HttpException
import ru.filit.motiv.app.states.main.ChangePassState
import ru.filit.motiv.app.utils.isConnect
import ru.filit.motiv.app.views.main.ChangePassView

class CostsEmailPresenter(val ctx: Context) :
    MviBasePresenter<CostsEmailView, CostsEmailState>() {

    private val subService = SubscriberInteractor(ctx)
    private val gson = Gson()

    override fun bindIntents() {
        val msisdnLoadIntent: Observable<CostsEmailState> =
            intent(CostsEmailView::msisdnLoadIntent).flatMap {
                if (!isConnect(ctx)){
                    return@flatMap Observable.just(CostsEmailState.InternetState(false))
                }
                subService.msisdnLoad()
                    .startWith(CostsEmailState.Loading)
                    .subscribeOn(Schedulers.io())
            }

        val emailSendIntent: Observable<CostsEmailState> =
            intent(CostsEmailView::sendEmailIntent).flatMap {
                if (!isConnect(ctx)){
                    return@flatMap Observable.just(CostsEmailState.InternetState(false))
                }
                subService.sendDetalEmail(it)
                    .startWith(CostsEmailState.Loading)
                    .subscribeOn(Schedulers.io())
                    .onErrorResumeNext { error: Throwable ->
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
                        Observable.just(CostsEmailState.ErrorShown(errMessage))
                    }
            }

        val changeInternetConnectionIntent: Observable<CostsEmailState> =
            intent (CostsEmailView::checkInternetConnectivityIntent).flatMap {
                Observable.just(CostsEmailState.InternetState(it))
            }

        val allIntents = Observable.merge(msisdnLoadIntent, emailSendIntent, changeInternetConnectionIntent)
            .observeOn(AndroidSchedulers.mainThread())


        subscribeViewState(allIntents, CostsEmailView::render)

    }


}