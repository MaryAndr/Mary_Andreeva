package ru.filit.motiv.app.views

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.filit.motiv.app.models.AuthModel
import ru.filit.motiv.app.states.EnterSMSPageState

interface EnterSMSPassView : MvpView  {

    fun resendSMSIntent() : Observable<String>

    fun authorizeIntent() : Observable<AuthModel>

    fun firstAttemptIntent() : Observable<Int>

    fun render(state: EnterSMSPageState)

}