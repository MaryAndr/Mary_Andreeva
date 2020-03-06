package ru.filit.motiv.app.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.filit.motiv.app.presenters.interactors.SubscriberInteractor
import ru.filit.motiv.app.states.main.MainPagePartialState
import ru.filit.motiv.app.states.main.MainPageState
import ru.filit.motiv.app.views.main.MainPageView
class MainPagePresenter(val ctx: Context) :
    MviBasePresenter<MainPageView, MainPageState>() {


    private val subService = SubscriberInteractor(ctx)


    override fun bindIntents() {

        val preLoadIntent: Observable<MainPagePartialState> =
            intent(MainPageView::preLoadIntent)
                .flatMap {
                    subService.preLoadData()
                        .startWith(
                            MainPagePartialState.Loading
                        ).subscribeOn(Schedulers.io())

                }

        val internetConnectionState : Observable<MainPagePartialState> = intent(MainPageView::checkInternetConnectivityIntent)
            .flatMap { Observable.just(MainPagePartialState.InternetState(it)) }


        val initialState = MainPageState(
            mainDataLoaded = false,
            errorShown = false,
            loading = false,
            connectionLost = false,
            connectionResume = false,
            appIsDeprecated = false
        )

        val allIntents = Observable.merge(preLoadIntent,
            internetConnectionState)
            .observeOn(AndroidSchedulers.mainThread())


        val stateObservable = allIntents.scan(initialState, this::viewStateReducer)

        subscribeViewState(stateObservable, MainPageView::render)
    }

    private fun viewStateReducer(
        previousState: MainPageState,
        changes: MainPagePartialState
    ): MainPageState {

        when (changes) {
            is MainPagePartialState.ShowDataState -> {
                previousState.errorShown = false
                previousState.errorText = null
                previousState.mainDataLoaded = true
                previousState.mainData = changes.data
                previousState.loading = false
                return previousState
            }
            is MainPagePartialState.ShowErrorMessage -> {
                previousState.errorShown = true
                previousState.errorText = changes.error
                previousState.appIsDeprecated = changes.appIsDeprecated
                previousState.mainDataLoaded = false
                previousState.mainData = null
                previousState.loading = false
                return previousState
            }
            is MainPagePartialState.Loading -> {
                previousState.errorShown = false
                previousState.errorText = null
                previousState.mainDataLoaded = false
                previousState.mainData = null
                previousState.loading = true
                return previousState
            }
            is MainPagePartialState.InternetState -> {
                previousState.errorShown = false
                previousState.errorText = null
                previousState.mainDataLoaded = false
                previousState.mainData = null
                previousState.loading = false
                previousState.connectionLost = !changes.active
                previousState.connectionResume = changes.active
                return previousState
            }
        }
    }

}