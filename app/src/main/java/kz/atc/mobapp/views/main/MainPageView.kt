package kz.atc.mobapp.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import kz.atc.mobapp.states.main.MainPageState

interface MainPageView: MvpView {

    fun preLoadIntent() : Observable<Int>
    
    fun render(state: MainPageState)

}