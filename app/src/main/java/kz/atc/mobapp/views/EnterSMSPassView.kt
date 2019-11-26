package kz.atc.mobapp.views

import android.widget.TextView
import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import kz.atc.mobapp.states.EnterSMSPageState

interface EnterSMSPassView : MvpView  {

    fun resendSMSIntent() : Observable<String>

    fun render(state: EnterSMSPageState)

}