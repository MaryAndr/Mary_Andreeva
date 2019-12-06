package kz.atc.mobapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.hannesdorfmann.mosby3.mvi.MviActivity
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kz.atc.mobapp.presenters.SplashScreenPresenter
import kz.atc.mobapp.states.SplashScreenState
import kz.atc.mobapp.views.SplashScreenView

class SplashScreen : MviActivity<SplashScreenView,SplashScreenPresenter>(), SplashScreenView{

    private lateinit var networkAvailabilityTrigger : BehaviorSubject<Int>

    override fun createPresenter() = SplashScreenPresenter(this)

    override fun checkInternetConnectivityIntent(): Observable<Int> {
       return networkAvailabilityTrigger
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
            Log.d("Debug", "Network is up")
            val homeIntent = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(homeIntent)
            finish()
        } else {
            Log.d("Debug", "Network is off")
            Toast.makeText(this, "Отсутствует соединение с интернетом", Toast.LENGTH_SHORT).show()
//            exitProcess(0)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        networkAvailabilityTrigger = BehaviorSubject.createDefault(0)

    }
}
