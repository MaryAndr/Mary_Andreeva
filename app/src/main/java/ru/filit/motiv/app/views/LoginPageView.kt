package ru.filit.motiv.app.views

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.models.AuthModel
import ru.filit.motiv.app.states.LoginPageState

interface LoginPageView : MvpView {

    fun checkInternetConnectivityIntent(): Observable<Unit>

    fun checkAuthIntent(): Observable<Int>

    fun authorizeIntent(): Observable<AuthModel>

    fun reenterIntent(): Observable<CharSequence>

    fun render(state: LoginPageState)
}