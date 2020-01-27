package ru.filit.motiv.app.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.filit.motiv.app.presenters.interactors.SubscriberInteractor
import ru.filit.motiv.app.states.main.ChangeTariffState
import ru.filit.motiv.app.views.main.ChangeTariffView

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