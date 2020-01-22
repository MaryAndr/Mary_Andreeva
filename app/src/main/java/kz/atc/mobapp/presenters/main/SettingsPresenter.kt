package kz.atc.mobapp.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.models.main.SettingsDataModel
import kz.atc.mobapp.presenters.interactors.SubscriberInteractor
import kz.atc.mobapp.states.main.SettingsState
import kz.atc.mobapp.views.main.SettingsView

class SettingsPresenter(val ctx: Context) : MviBasePresenter<SettingsView, SettingsState>() {

    private val subService = SubscriberInteractor(ctx)

    override fun bindIntents() {

        val fetchMainData: Observable<SettingsState> =
            intent(SettingsView::mainDataLoadingIntent)
                .flatMap {
                    subService.getSettingsMainData()
                        .map<SettingsState> {
                            SettingsState.MainDataLoaded(it)
                        }
                        .subscribeOn(Schedulers.io())
                }
                .startWith (SettingsState.Loading)

        val allIntents = fetchMainData
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(allIntents, SettingsView::render)
    }

}