package kz.atc.mobapp.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.presenters.interactors.SubscriberInteractor
import kz.atc.mobapp.states.main.CostAndReplenishmentPartialState
import kz.atc.mobapp.states.main.CostAndReplenishmentState
import kz.atc.mobapp.views.main.CostAndReplenishmentView

class CostsAndReplenishmentPresenter(val ctx: Context) :
    MviBasePresenter<CostAndReplenishmentView, CostAndReplenishmentState>() {

    private val subService = SubscriberInteractor(ctx)

    override fun bindIntents() {

        var mainDataLoadIntent: Observable<CostAndReplenishmentPartialState> =
            intent(CostAndReplenishmentView::mainDataLoadIntent)
                .flatMap {
                    subService.costsMainData().subscribeOn(Schedulers.io())
                }

        val initialState = CostAndReplenishmentState(false,null,false,null)

        val allIntents = mainDataLoadIntent
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
                previousState.errorShown = false
                previousState.errorText = null
                previousState.mainDataLoaded = true
                previousState.mainData = changes.data
                return previousState
            }
        }
    }

}