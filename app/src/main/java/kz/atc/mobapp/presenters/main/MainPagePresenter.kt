package kz.atc.mobapp.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.presenters.interactors.SubscriberInteractor
import kz.atc.mobapp.states.main.MainPagePartialState
import kz.atc.mobapp.states.main.MainPageState
import kz.atc.mobapp.views.main.MainPageView

class MainPagePresenter(val ctx: Context) :
    MviBasePresenter<MainPageView, MainPageState>() {


    private val subService = SubscriberInteractor(ctx)


    override fun bindIntents() {

        var preLoadIntent : Observable<MainPagePartialState> =
            intent(MainPageView::preLoadIntent)
                .flatMap {
                    subService.preLoadData().subscribeOn(Schedulers.io())
                }

        val initialState = MainPageState(
            mainDataLoaded = false,
            errorShown = false)

        val allIntents = preLoadIntent
            .observeOn(AndroidSchedulers.mainThread())


        val stateObservable = allIntents.scan(initialState, this::viewStateReducer)

        subscribeViewState(stateObservable, MainPageView::render)
    }

    private fun viewStateReducer(
        previousState: MainPageState,
        changes: MainPagePartialState) : MainPageState {

        when(changes) {
            is MainPagePartialState.ShowDataState -> {
                previousState.errorShown = false
                previousState.errrorText = null
                previousState.mainDataLoaded = true
                previousState.mainData = changes.data
                return previousState
            }
            is MainPagePartialState.ShowErrorMessage -> {
                previousState.errorShown = true
                previousState.errrorText = changes.error
                previousState.mainDataLoaded = false
                previousState.mainData = null
                return previousState
            }
        }
    }

}