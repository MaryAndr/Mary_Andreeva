package ru.filit.motiv.app.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.filit.motiv.app.presenters.interactors.SubscriberInteractor
import ru.filit.motiv.app.states.main.SettingsState
import ru.filit.motiv.app.utils.isConnect
import ru.filit.motiv.app.views.main.SettingsView

class SettingsPresenter(val ctx: Context) : MviBasePresenter<SettingsView, SettingsState>() {

    private val subService = SubscriberInteractor(ctx)

    override fun bindIntents() {

        val fetchMainData: Observable<SettingsState> =
            intent(SettingsView::mainDataLoadingIntent)
                .flatMap {
                    if (!isConnect(ctx)){
                        return@flatMap Observable.just(SettingsState.InternetState(active = false))
                    }
                    subService.getSettingsMainData()
                        .subscribeOn(Schedulers.io())
                }
                .startWith (SettingsState.Loading)

        val logoutIntent: Observable<SettingsState> =
            intent(SettingsView::logoutIntent)
                .flatMap{
                    subService.subService.logout().flatMap {
                        Observable.just(SettingsState.LogOut)
                    }.subscribeOn(Schedulers.io())
                }
        val changeInternetConnectionIntent: Observable<SettingsState> =
            intent (SettingsView::checkInternetConnectivityIntent).flatMap {
                Observable.just(SettingsState.InternetState(it))
            }

        val allIntents = Observable.merge(fetchMainData, logoutIntent, changeInternetConnectionIntent)
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(allIntents, SettingsView::render)
    }

}