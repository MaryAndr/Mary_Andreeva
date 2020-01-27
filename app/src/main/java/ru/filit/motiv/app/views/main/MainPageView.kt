package ru.filit.motiv.app.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.states.main.MainPageState

interface MainPageView: MvpView {

    fun preLoadIntent() : Observable<Int>
    
    fun render(state: MainPageState)

}