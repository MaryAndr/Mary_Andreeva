package ru.filit.motiv.app.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.filit.motiv.app.presenters.interactors.SubscriberInteractor
import ru.filit.motiv.app.states.main.FAQState
import ru.filit.motiv.app.views.main.FAQView

class FAQPresenter(val ctx: Context): MviBasePresenter<FAQView,FAQState>() {

    private val subService = SubscriberInteractor(ctx)

    override fun bindIntents() {
        val fetchFAQIntent: Observable<FAQState> =
            intent (FAQView::getFAQIntent).flatMap {
                subService.getFAQ()
                    .subscribeOn(Schedulers.computation())
                    .startWith(FAQState.Loading)
            }

        val changeInternetConnectionIntent: Observable<FAQState> =
            intent (FAQView::checkInternetConnectivityIntent).flatMap {

                Observable.just(FAQState.InternetState(it))
            }

        val allIntents: Observable<FAQState> =
            Observable.merge(fetchFAQIntent, changeInternetConnectionIntent)
                .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(allIntents, FAQView::render)

    }


}