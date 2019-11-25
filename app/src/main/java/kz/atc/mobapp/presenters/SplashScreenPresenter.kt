package kz.atc.mobapp.presenters

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.states.SplashScreenState
import kz.atc.mobapp.utils.CheckInternetConnectivity
import kz.atc.mobapp.views.SplashScreenView
import java.util.concurrent.TimeUnit

class SplashScreenPresenter(val ctx : Context) : MviBasePresenter<SplashScreenView, SplashScreenState> () {

    override fun bindIntents() {

        val splashScreenState : Observable<SplashScreenState> = intent(SplashScreenView::checkInternetConnectivityIntent)
            .subscribeOn(Schedulers.io())
            .debounce ( 400, TimeUnit.MILLISECONDS )
            .switchMap{
                CheckInternetConnectivity.getNetworkStatus(ctx)
            }
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(splashScreenState, SplashScreenView :: render)
    }

}