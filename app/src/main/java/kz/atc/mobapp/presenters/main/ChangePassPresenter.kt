package kz.atc.mobapp.presenters.main

import android.content.Context
import com.google.gson.Gson
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.models.ErrorJson
import kz.atc.mobapp.models.main.ChangePassRequest
import kz.atc.mobapp.presenters.interactors.SubscriberInteractor
import kz.atc.mobapp.states.main.ChangePassState
import kz.atc.mobapp.views.main.ChangePassView
import retrofit2.HttpException

class ChangePassPresenter(val ctx: Context) :
    MviBasePresenter<ChangePassView, ChangePassState>() {

    private val subService = SubscriberInteractor(ctx)
    private val gson = Gson()

    override fun bindIntents() {
        val processRequestIntent: Observable<ChangePassState> =
            intent(ChangePassView::processIntent)
                .flatMap {
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

        val allIntents = processRequestIntent
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(allIntents, ChangePassView::render)

    }
}