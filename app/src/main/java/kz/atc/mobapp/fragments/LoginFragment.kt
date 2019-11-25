package kz.atc.mobapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_login.*

import kz.atc.mobapp.R
import kz.atc.mobapp.models.AuthModel
import kz.atc.mobapp.presenters.LoginPagePresenter
import kz.atc.mobapp.states.LoginPageState
import kz.atc.mobapp.utils.PhoneTextWatcher
import kz.atc.mobapp.utils.TextConverter
import kz.atc.mobapp.views.LoginPageView
import java.util.concurrent.TimeUnit


class LoginFragment : MviFragment<LoginPageView, LoginPagePresenter>(), LoginPageView {

    override fun reenterIntent(): Observable<CharSequence> {
        return Observable.merge(
            RxTextView.textChanges(etLoginPhone),
            RxTextView.textChanges(etPassword)
        )
    }


    override fun authorizeIntent(): Observable<AuthModel> {
        return RxView.clicks(buttonAuth)
            .map<AuthModel> {
                AuthModel(
                    TextConverter().getOnlyDigits(etLoginPhone.text.toString()),
                    etPassword.text.toString()
                )
            }
    }

    override fun createPresenter() = LoginPagePresenter(context!!)

    override fun checkInternetConnectivityIntent(): Observable<Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun render(state: LoginPageState) {
        when {
            state.errorStateShown -> {
                tvAuthErr.visibility = View.VISIBLE
                tvAuthErr.text = state.errorMessage
                etLoginPhone.setBackgroundResource(R.drawable.login_et_shape_error)
                etPassword.setBackgroundResource(R.drawable.login_et_shape_error)
            }
            state.successFullyAuthorized -> {
                tvAuthErr.visibility = View.VISIBLE
                tvAuthErr.text = "AUTHORIZED"
            }
            state.defaultState -> {
                tvAuthErr.visibility = View.GONE
                etLoginPhone.setBackgroundResource(R.drawable.login_et_shape)
                etPassword.setBackgroundResource(R.drawable.login_et_shape)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        etLoginPhone.addTextChangedListener(PhoneTextWatcher(etLoginPhone))
    }
}
