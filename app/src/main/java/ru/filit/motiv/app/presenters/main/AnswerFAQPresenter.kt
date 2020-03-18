package ru.filit.motiv.app.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.filit.motiv.app.presenters.interactors.SubscriberInteractor
import ru.filit.motiv.app.states.main.AnswerFAQState
import ru.filit.motiv.app.views.main.AnswerFAQView

class AnswerFAQPresenter(val ctx: Context) : MviBasePresenter<AnswerFAQView, AnswerFAQState>() {

    private val subService = SubscriberInteractor(ctx)

    override fun bindIntents() {
        val fetchFAQIntent: Observable<AnswerFAQState> =
            intent(AnswerFAQView::getAnswerIntent).flatMap {
                subService.getAnswerFAQ(it)
                    .subscribeOn(Schedulers.computation())
                    .startWith(AnswerFAQState.Loading)
            }

        val changeInternetConnectionIntent: Observable<AnswerFAQState> =
            intent(AnswerFAQView::checkInternetConnectivityIntent).flatMap {

                Observable.just(AnswerFAQState.InternetState(it))
            }

        val allIntents: Observable<AnswerFAQState> =
            Observable.merge(fetchFAQIntent, changeInternetConnectionIntent)
                .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(allIntents, AnswerFAQView::render)

    }


}
