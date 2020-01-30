package ru.filit.motiv.app.presenters.main

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import ru.filit.motiv.app.states.main.HelpState
import ru.filit.motiv.app.utils.AvailableMessenger
import ru.filit.motiv.app.views.main.HelpView

class HelpPresenter (val ctx: Context?): MviBasePresenter<HelpView, HelpState>() {


    override fun bindIntents() {
        val fetchMessengers: Observable<HelpState> = intent(HelpView::getMessengersIntent)
            .flatMap {Observable.just(AvailableMessenger.getListAvailableMessenger(ctx))}
            .map { when{
                it.size == 2 -> HelpState.BothMessengers
                it.contains("viber")-> HelpState.ViberMessengers
                it.contains("whatsapp")-> HelpState.WhatsappMessengers
                else -> HelpState.NoMessengers
            } }
        subscribeViewState(fetchMessengers, HelpView::render)
    }

}