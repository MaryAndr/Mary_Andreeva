package kz.atc.mobapp.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import kz.atc.mobapp.states.main.ServicesState

interface ServicesPageView : MvpView {

    fun showEnabledServiceIntent() : Observable<Boolean>

    fun render(state: ServicesState)
}