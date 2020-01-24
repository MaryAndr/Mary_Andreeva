package kz.atc.mobapp.presenters.main

import android.content.Context
import com.google.gson.Gson
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.models.ErrorJson
import kz.atc.mobapp.models.main.BlockUnblockRequest
import kz.atc.mobapp.presenters.interactors.SubscriberInteractor
import kz.atc.mobapp.states.main.BlockUnblockDialogState
import kz.atc.mobapp.views.main.BlockUnblockDialogView
import retrofit2.HttpException

class BlockUnblockDialogPresenter(val ctx: Context) :
    MviBasePresenter<BlockUnblockDialogView, BlockUnblockDialogState>() {

    private val subService = SubscriberInteractor(ctx)
    private val gson = Gson()

    override fun bindIntents() {
        val processRequestIntent: Observable<BlockUnblockDialogState> =
            intent(BlockUnblockDialogView::processIntent)
                .flatMap {
                    val request = BlockUnblockRequest()
                    if (it.isBlock) {
                        request.block = "true"
                        request.reason_desc = it.reason
                    } else {
                        request.block = "false"
                        request.codeword = it.codeword
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