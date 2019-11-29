package kz.atc.mobapp.presenters

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.models.ErrorJson
import kz.atc.mobapp.presenters.interactors.UserInteractor
import kz.atc.mobapp.states.SendSMSPageState
import kz.atc.mobapp.views.SendSMSScreenView
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.HttpException

class SendSMSPagePresenter(val ctx: Context) :
    MviBasePresenter<SendSMSScreenView, SendSMSPageState>() {

    private val gson = Gson()

    override fun bindIntents() {
        val sendSMSScreenState: Observable<SendSMSPageState> =
            intent(SendSMSScreenView::sendSMSButtonIntent).flatMap { auth ->
                if (auth.isBlank()) {
                    Observable.just(SendSMSPageState.ErrorState("Введите номер телефона"))
                } else {
                    UserInteractor().userService.userTypeCheck(auth).flatMap { result ->
                        if (result.name != "Пользователь мобильной связи") {
                            Observable.just(SendSMSPageState.ErrorState("Вы не являетесь пользователем мобильной связи"))
                        } else {
                            UserInteractor().userService.sendSMS(
                                RequestBody.create(
                                    MediaType.parse(
                                        "text/plain"
                                    ), auth
                                )
                            ).flatMap {
                                Observable.just(SendSMSPageState.SmsSend)
                            }
                        }
                    }
                        .onErrorResumeNext { error: Throwable ->
                            var errMessage = error.localizedMessage
                            if (error is HttpException) {
                                if (error is HttpException) {
                                    errMessage = if (error.code() == 409) {
                                        "Вы не являетесь пользователем мобильной связи"
                                    } else {
                                        val errorBody = error.response()!!.errorBody()

                                        val adapter =
                                            gson.getAdapter<ErrorJson>(ErrorJson::class.java!!)
                                        Log.d("JSON", errorBody.toString())
                                        val errorObj = adapter.fromJson(errorBody!!.string())
                                        errorObj.error_description
                                    }
                                }
                            }
                            Observable.just(SendSMSPageState.ErrorState(errMessage))
                        }.subscribeOn(Schedulers.io())
                }
            }

        val defaultState: Observable<SendSMSPageState> =
            intent(SendSMSScreenView::defaultIntent).map<SendSMSPageState> {
                SendSMSPageState.DefaultState
            }

        val allIntents = Observable.merge(sendSMSScreenState, defaultState)
            .observeOn(AndroidSchedulers.mainThread())


        subscribeViewState(allIntents, SendSMSScreenView::render)
    }
}