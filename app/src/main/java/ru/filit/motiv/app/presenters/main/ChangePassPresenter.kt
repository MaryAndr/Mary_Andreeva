package ru.filit.motiv.app.presenters.main

import android.content.Context
import com.google.gson.Gson
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.filit.motiv.app.models.ErrorJson
import ru.filit.motiv.app.models.main.ChangePassRequest
import ru.filit.motiv.app.presenters.interactors.SubscriberInteractor
import ru.filit.motiv.app.states.main.ChangePassState
import ru.filit.motiv.app.views.main.ChangePassView
import retrofit2.HttpException
import ru.filit.motiv.app.utils.isConnect

class ChangePassPresenter(val ctx: Context) :
    MviBasePresenter<ChangePassView, ChangePassState>() {

    private val subService = SubscriberInteractor(ctx)
    private val gson = Gson()

    override fun bindIntents() {
        val processRequestIntent: Observable<ChangePassState> =
            intent(ChangePassView::processIntent)
                .flatMap {
                    if (!isConnect(ctx)){
                        return@flatMap Observable.just(ChangePassState.InternetState(false))
                    }
                    var curErr: String? = null
                    var newErr: String? = null
                    if (it.currentPass.length < 8) {
                        curErr = "Пароль должен содержать не менее 8 символов"
                    }
                    if (it.newPass.length < 8) {
                        newErr = "Пароль должен содержать не менее 8 символов"
                    }

                    if (curErr != null || newErr != null) {
                        Observable.just(ChangePassState.ValidationError(newErr, curErr))
                    } else {
                        subService.subService.changePass(
                            ChangePassRequest(it.newPass, it.currentPass)
                        ).map<ChangePassState> {
                            ChangePassState.Processed("Пароль успешно изменен")
                        }.subscribeOn(Schedulers.io())
                            .onErrorReturn { error: Throwable ->
                            if (error is HttpException) {
                                val errorBody = error.response()!!.errorBody()

                                val adapter =
                                    gson.getAdapter<ErrorJson>(ErrorJson::class.java!!)
                                val errorObj = adapter.fromJson(errorBody!!.string())
                                ChangePassState.Processed(errorObj.error_description)
                            } else {
                                ChangePassState.Processed("Произошла непредвиденная ошибка")
                            }
                        }.startWith (
                            ChangePassState.Loading
                        )
                    }
                }
        val changeInternetConnectionIntent: Observable<ChangePassState> =
            intent (ChangePassView::checkInternetConnectivityIntent).flatMap {
                Observable.just(ChangePassState.InternetState(it))
            }

        val allIntents = Observable.merge(processRequestIntent, changeInternetConnectionIntent)
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(allIntents, ChangePassView::render)

    }
}