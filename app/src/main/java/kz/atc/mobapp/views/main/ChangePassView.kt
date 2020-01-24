package kz.atc.mobapp.views.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import kz.atc.mobapp.models.main.ChangePassModel
import kz.atc.mobapp.states.main.ChangePassState

interface ChangePassView : MvpView {

    fun processIntent() : Observable<ChangePassModel>

    fun render(state: ChangePassState)

}