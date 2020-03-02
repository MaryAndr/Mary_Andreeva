package ru.filit.motiv.app.presenters.main

import android.content.Context
import com.google.gson.Gson
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.filit.motiv.app.models.ErrorJson
import ru.filit.motiv.app.models.main.BlockUnblockRequest
import ru.filit.motiv.app.presenters.interactors.SubscriberInteractor
import ru.filit.motiv.app.states.main.BlockUnblockDialogState
import ru.filit.motiv.app.views.main.BlockUnblockDialogView
import retrofit2.HttpException
import ru.filit.motiv.app.utils.isConnect

class BlockUnblockDialogPresenter(val ctx: Context) :
    MviBasePresenter<BlockUnblockDialogView, BlockUnblockDialogState>() {

    private val subService = SubscriberInteractor(ctx)
    private val gson = Gson()

    override fun bindIntents() {
        val processRequestIntent: Observable<BlockUnblockDialogState> =
            intent(BlockUnblockDialogView::processIntent)
                .flatMap {
                    if (!isConnect(ctx= ctx)){
                        return@flatMap Observable.just(BlockUnblockDialogState.InternetState(false))
                    }
                    val request = BlockUnblockRequest()
                    if (it.isBlock) {
                        request.block = "true"
                        request.reason_desc = it.reason
                    } else {
                        if (!it.codeword.isNullOrEmpty()){
                        request.block = "false"
                        request.codeword = it.codeword
                        }else{
                           return@flatMap Observable.just(BlockUnblockDialogState.RequestProcessed(
                                message = "Введите кодовое слово",
                               isProcessed = false
                            ))
                        }
                    }
                    subService.subService.changeStatus(request)
                        .map<BlockUnblockDialogState> {
                            BlockUnblockDialogState.RequestProcessed(it.status.name, null, true)
                        }
                        .subscribeOn(Schedulers.io()).onErrorReturn { error: Throwable ->
                            var errMessage = error.localizedMessage
                            if (error is HttpException) {
                                val errorBody = error.response()!!.errorBody()

                                val adapter =
                                    gson.getAdapter<ErrorJson>(ErrorJson::class.java!!)
                                val errorObj = adapter.fromJson(errorBody!!.string())

                                if (error.code() == 409) {
                                    BlockUnblockDialogState.RequestProcessed(
                                        errorObj.error_description,
                                        errorObj.additional_info.count,
                                        false
                                    )
                                } else {
                                    BlockUnblockDialogState.RequestProcessed(
                                        errorObj.error_description,
                                        null,
                                        false
                                    )
                                }
                            } else {
                                BlockUnblockDialogState.RequestProcessed(
                                    "Произошла непредвиденная ошибка",
                                    null,
                                    false
                                )
                            }
                        }
                }
                .startWith(BlockUnblockDialogState.Loading)

        val allIntents = processRequestIntent
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(allIntents, BlockUnblockDialogView::render)
    }
}