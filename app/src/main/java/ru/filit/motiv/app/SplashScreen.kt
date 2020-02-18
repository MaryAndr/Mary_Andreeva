package ru.filit.motiv.app

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.hannesdorfmann.mosby3.mvi.MviActivity
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_splash_screen.*
import ru.filit.motiv.app.presenters.SplashScreenPresenter
import ru.filit.motiv.app.states.SplashScreenState
import ru.filit.motiv.app.utils.ConnectivityReceiver
import ru.filit.motiv.app.utils.isConnect
import ru.filit.motiv.app.views.SplashScreenView

class SplashScreen : MviActivity<SplashScreenView,SplashScreenPresenter>(), SplashScreenView, ConnectivityReceiver.ConnectivityReceiverListener {

    override fun createPresenter() = SplashScreenPresenter(this)

    private lateinit var networkAvailabilityTrigger : BehaviorSubject<Boolean>
    private val connectivityReceiver = ConnectivityReceiver()

    override fun checkInternetConnectivityIntent(): Observable<Boolean> {
       return networkAvailabilityTrigger
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            networkAvailabilityTrigger.onNext(true)
        }
    }

    override fun render(state: SplashScreenState) {
        when(state) {
            is SplashScreenState.InternetState -> {
                renderNetwork(state)
            }
        }
    }

    private fun renderNetwork(state: SplashScreenState.InternetState) {
        if(state.active) {
            val homeIntent = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(homeIntent)
            finish()
        } else {
            splash_screen.visibility = View.GONE
            no_internet_view.visibility = View.VISIBLE
//            exitProcess(0)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        networkAvailabilityTrigger = BehaviorSubject.createDefault(isConnect(this))
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))


    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
    }
}
