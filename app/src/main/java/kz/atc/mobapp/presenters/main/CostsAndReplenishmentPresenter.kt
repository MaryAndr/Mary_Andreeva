package kz.atc.mobapp.presenters.main

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.models.ErrorJson
import kz.atc.mobapp.presenters.interactors.SubscriberInteractor
import kz.atc.mobapp.states.main.CostAndReplenishmentPartialState
import kz.atc.mobapp.states.main.CostAndReplenishmentState
import kz.atc.mobapp.views.main.CostAndReplenishmentView
import retrofit2.HttpException

class CostsAndReplenishmentPresenter(val ctx: Context) :
    MviBasePresenter<CostAndReplenishmentView, CostAndReplenishmentState>() {

    private val subService = SubscriberInteractor(ctx)
    private val gson = Gson()
    override fun bindIntents() {

        var mainDataLoadIntent: Observable<CostAndReplenishmentPartialState> =
            intent(CostAndReplenishmentView::mainDataLoadIntent)
                .flatMap {
                    subService.costsMainData().subscribeOn(Schedulers.io())
                }.startWith(
                    CostAndReplenishmentPartialState.Loading
                )

        var costsShownIntent: Observable<CostAndReplenishmentPartialState> =
            intent(CostAndReplenishmentView::showCostsIntent)
                .flatMap {
                    Log.d("Debug", "Costs Layout Triggered")
                    Observable.just(CostAndReplenishmentPartialState.ShowCostsLayout)
                }

        var replenishmentShownIntent: Observable<CostAndReplenishmentPartialState> =
            intent(CostAndReplenishmentView::showReplenishmentIntent)
                .flatMap {
                    Log.d("Debug", "Rep Layout Triggered")
                    Observable.just(CostAndReplenishmentPartialState.ShowReplenishmentLayout)
                }

        var replenishmentDataShownIntent: Observable<CostAndReplenishmentPartialState> =
            intent(CostAndReplenishmentView::getReplenishmentDataIntent)
                .flatMap {
                    subService.getReplenishmentData(it).subscribeOn(Schedulers.io()).onErrorResumeNext { error: Throwable ->
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
                        Observable.just(CostAndReplenishmentPartialState.ShowErrorState(errMessage))
                    }
                }


        val initialState = CostAndReplenishmentState(
            false, null, false, replenishmentShown = false,
            errorShown = false,
            errorText = null,
            replenishmentDataLoaded = false,
            replenishmentData = null,
            loading = false
        )

        val allIntents = Observable.merge(
            mainDataLoadIntent,
            costsShownIntent,
            replenishmentShownIntent,
            replenishmentDataShownIntent
        )
            .observeOn(AndroidSchedulers.mainThread())

        val stateObservable = allIntents.scan(initialState, this::viewStateReducer)
        subscribeViewState(stateObservable, CostAndReplenishmentView::render)
    }

    private fun viewStateReducer(
        previousState: CostAndReplenishmentState,
        changes: CostAndReplenishmentPartialState
    ): CostAndReplenishmentState {

        when (changes) {
            is CostAndReplenishmentPartialState.ShowMainDataState -> {
                previousState.loading = false
                previousState.errorShown = false
                previousState.errorText = null
                previousState.mainDataLoaded = true
                previousState.mainData = changes.data
                previousState.replenishmentDataLoaded = false
                Log.d("Debug", "DATA")
                return previousState
            }
            is CostAndReplenishmentPartialState.ShowCostsLayout -> {
                previousState.errorShown = false
                previousState.errorText = null
                previousState.costsShown = true
                previousState.mainDataLoaded = false
                previousState.replenishmentShown = false
                previousState.replenishmentDataLoaded = false

                Log.d("Debug", "COSTS")
                return previousState
            }
            is CostAndReplenishmentPartialState.ShowReplenishmentLayout -> {
                previousState.errorShown = false
                previousState.errorText = null
                previousState.costsShown = false
                previousState.mainDataLoaded = false
                previousState.replenishmentShown = true
                previousState.replenishmentDataLoaded = false
                Log.d("Debug", "REPLENISHMENT")
                return previousState
            }
            is CostAndReplenishmentPartialState.ShowReplenishmentData -> {
                previousState.errorShown = false
                previousState.errorText = null
                previousState.replenishmentDataLoaded = true
                previousState.costsShown = false
                previousState.mainDataLoaded = false
                previousState.replenishmentShown = false
                previousState.replenishmentData = changes.payments
                return previousState
            }
            is CostAndReplenishmentPartialState.ShowErrorState -> {
                previousState.loading = false
                previousState.errorShown = true
                previousState.errorText = changes.error
                previousState.replenishmentDataLoaded = false
                previousState.costsShown = false
                previousState.mainDataLoaded = false
                previousState.replenishmentShown = false
                return previousState
            }
            is CostAndReplenishmentPartialState.Loading -> {
                previousState.loading = true
                previousState.errorShown = false
                previousState.replenishmentDataLoaded = false
                previousState.costsShown = false
                previousState.mainDataLoaded = false
                previousState.replenishmentShown = false
                return previousState
            }
        }
    }

}