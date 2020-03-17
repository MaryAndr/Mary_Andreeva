package ru.filit.motiv.app.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import ru.filit.motiv.app.states.main.SupportState
import ru.filit.motiv.app.utils.AvailableMessenger
import ru.filit.motiv.app.views.main.SupportView

class SupportPresenter (val ctx: Context?): MviBasePresenter<SupportView, SupportState>() {


    override fun bindIntents() {
        val fetchMessengers: Observable<SupportState> = intent(SupportView::getMessengersIntent)
            .flatMap {Observable.just(AvailableMessenger.getListAvailableMessenger(ctx))}
            .map { when{
                it.size == 2 -> SupportState.BothMessengers
                it.contains("viber")-> SupportState.ViberMessengers
                it.contains("whatsapp")-> SupportState.WhatsappMessengers
                else -> SupportState.NoMessengers
            } }
        subscribeViewState(fetchMessengers, SupportView::render)
    }

}