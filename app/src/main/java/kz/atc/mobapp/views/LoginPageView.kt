package kz.atc.mobapp.views

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import kz.atc.mobapp.models.AuthModel
import kz.atc.mobapp.states.LoginPageState

interface LoginPageView : MvpView {

    fun checkInternetConnectivityIntent(): Observable<Unit>

    fun authorizeIntent(): Observable<AuthModel>

    fun reenterIntent(): Observable<CharSequence>

    fun render(state: LoginPageState)
}