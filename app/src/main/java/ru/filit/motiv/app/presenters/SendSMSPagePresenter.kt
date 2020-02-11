package ru.filit.motiv.app.presenters

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.filit.motiv.app.models.ErrorJson
import ru.filit.motiv.app.presenters.interactors.UserInteractor
import ru.filit.motiv.app.states.SendSMSPageState
import ru.filit.motiv.app.views.SendSMSScreenView
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
                    UserInteractor(ctx).userService.userTypeCheck(auth).flatMap { result ->
                        if (result.name != "Пользователь мобильной связи") {
                            Observable.just(SendSMSPageState.ErrorState("Вы не являетесь пользователем мобильной связи"))
                        } else {
                            UserInteractor(ctx).userService.sendSMS(
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