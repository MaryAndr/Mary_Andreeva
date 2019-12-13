package kz.atc.mobapp.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.presenters.interactors.SubscriberInteractor
import kz.atc.mobapp.states.main.CostsEmailState
import kz.atc.mobapp.views.main.CostsEmailView

class CostsEmailPresenter(val ctx: Context) :
    MviBasePresenter<CostsEmailView, CostsEmailState>() {

    private val subService = SubscriberInteractor(ctx)

    override fun bindIntents() {
        val msisdnLoadIntent: Observable<CostsEmailState> =
            intent(CostsEmailView::msisdnLoadIntent).flatMap {
                subService.msisdnLoad().subscribeOn(Schedulers.io())
            }

        val allIntents = msisdnLoadIntent
            .observeOn(AndroidSchedulers.mainThread())


        subscribeViewState(allIntents, CostsEmailView::render)

    }


}