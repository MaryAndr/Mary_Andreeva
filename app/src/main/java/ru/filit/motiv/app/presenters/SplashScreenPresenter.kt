package ru.filit.motiv.app.presenters

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.filit.motiv.app.states.SplashScreenState
import ru.filit.motiv.app.utils.CheckInternetConnectivity
import ru.filit.motiv.app.views.SplashScreenView
import java.util.concurrent.TimeUnit

class SplashScreenPresenter(val ctx : Context) : MviBasePresenter<SplashScreenView, SplashScreenState> () {

    override fun bindIntents() {

        val splashScreenState : Observable<SplashScreenState> = intent(SplashScreenView::checkInternetConnectivityIntent)
            .subscribeOn(Schedulers.io())
            .debounce(400,TimeUnit.MILLISECONDS)
            .switchMap{
                CheckInternetConnectivity.getNetworkStatus(ctx)
            }
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(splashScreenState, SplashScreenView :: render)
    }

}