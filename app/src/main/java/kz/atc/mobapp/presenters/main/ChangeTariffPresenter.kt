package kz.atc.mobapp.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.presenters.interactors.SubscriberInteractor
import kz.atc.mobapp.states.main.ChangeTariffState
import kz.atc.mobapp.views.main.ChangeTariffView

class ChangeTariffPresenter(val ctx: Context) :
    MviBasePresenter<ChangeTariffView, ChangeTariffState>() {

    private val subService = SubscriberInteractor(ctx)

    override fun bindIntents() {
        val mainDataIntent: Observable<ChangeTariffState> =
            intent(ChangeTariffView::showMainDataIntent).flatMap {
                subService.getTariffs().subscribeOn(Schedulers.io())
            }
        val allIntents = mainDataIntent
            .observeOn(AndroidSchedulers.mainThread())


        subscribeViewState(allIntents, ChangeTariffView::render)
    }

}