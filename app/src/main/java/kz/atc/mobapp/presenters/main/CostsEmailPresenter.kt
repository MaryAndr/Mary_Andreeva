package kz.atc.mobapp.presenters.main

import android.content.Context
import com.google.gson.Gson
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.models.ErrorJson
import kz.atc.mobapp.presenters.interactors.SubscriberInteractor
import kz.atc.mobapp.states.main.CostsEmailState
import kz.atc.mobapp.views.main.CostsEmailView
import retrofit2.HttpException

class CostsEmailPresenter(val ctx: Context) :
    MviBasePresenter<CostsEmailView, CostsEmailState>() {

    private val subService = SubscriberInteractor(ctx)
    private val gson = Gson()

    override fun bindIntents() {
        val msisdnLoadIntent: Observable<CostsEmailState> =
            intent(CostsEmailView::msisdnLoadIntent).flatMap {
                subService.msisdnLoad().subscribeOn(Schedulers.io())
            }

        val emailSendIntent: Observable<CostsEmailState> =
            intent(CostsEmailView::sendEmailIntent).flatMap {
                subService.sendDetalEmail(it).subscribeOn(Schedulers.io())
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

        val allIntents = Observable.merge(msisdnLoadIntent, emailSendIntent)
            .observeOn(AndroidSchedulers.mainThread())


        subscribeViewState(allIntents, CostsEmailView::render)

    }


}