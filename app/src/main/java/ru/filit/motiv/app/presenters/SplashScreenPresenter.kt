package ru.filit.motiv.app.presenters

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.filit.motiv.app.states.SplashScreenState
import ru.filit.motiv.app.views.SplashScreenView

class SplashScreenPresenter(val ctx : Context) : MviBasePresenter<SplashScreenView, SplashScreenState> (){

    override fun bindIntents() {

        val splashScreenState : Observable<SplashScreenState> = intent(SplashScreenView::checkInternetConnectivityIntent)
            .flatMap {
                Observable.just(SplashScreenState.InternetState(it) as SplashScreenState)
                .observeOn(AndroidSchedulers.mainThread())
            }

        subscribeViewState(splashScreenState, SplashScreenView :: render)
    }

}