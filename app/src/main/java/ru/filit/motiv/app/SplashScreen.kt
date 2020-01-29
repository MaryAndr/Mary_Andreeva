package ru.filit.motiv.app

import android.content.Intent
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
import ru.filit.motiv.app.views.SplashScreenView

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
            splash_screen.visibility = View.VISIBLE
            no_internet_view.visibility = View.GONE
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

        networkAvailabilityTrigger = BehaviorSubject.createDefault(0)

    }
}
