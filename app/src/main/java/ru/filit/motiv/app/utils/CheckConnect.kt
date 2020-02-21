package ru.filit.motiv.app.utils

import android.content.Context
import android.net.ConnectivityManager
import io.reactivex.Observable
import ru.filit.motiv.app.states.SplashScreenState
import java.util.concurrent.TimeUnit

fun isConnect(ctx: Context):Boolean{
    val connMgr =ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connMgr.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnectedOrConnecting
}


    fun getNetworkStatus(ctx: Context): Observable<SplashScreenState> {
        return Observable.interval(0,5, TimeUnit.SECONDS).flatMap {
                Observable.just(SplashScreenState.InternetState(isConnect(ctx)))
        }
    }


