package ru.filit.motiv.app.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import io.reactivex.Observable
import ru.filit.motiv.app.states.SplashScreenState
import java.util.concurrent.TimeUnit

object CheckInternetConnectivity {

    fun getNetworkStatus(ctx: Context): Observable<SplashScreenState> {
        return Observable.interval(0,5, TimeUnit.SECONDS).flatMap {
            checkInternetConnectivity(ctx)
                .map<SplashScreenState> {
                    SplashScreenState.InternetState(it)
                }
        }
    }

    private fun checkInternetConnectivity(ctx: Context): Observable<Boolean> {
        val connectivity = ctx.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null)
                for (i in info)
                    if (i.state == NetworkInfo.State.CONNECTED) {
                        return Observable.just(true)
                    }
        }
        return Observable.just(false)
    }

}