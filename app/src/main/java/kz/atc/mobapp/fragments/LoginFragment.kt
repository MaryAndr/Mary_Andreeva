package kz.atc.mobapp.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_login.*
import kz.atc.mobapp.MainPageActivity
import kz.atc.mobapp.R
import kz.atc.mobapp.models.AuthModel
import kz.atc.mobapp.presenters.LoginPagePresenter
import kz.atc.mobapp.states.LoginPageState
import kz.atc.mobapp.utils.PhoneTextWatcher
import kz.atc.mobapp.utils.TextConverter
import kz.atc.mobapp.views.LoginPageView

class LoginFragment : MviFragment<LoginPageView, LoginPagePresenter>(), LoginPageView {

    private lateinit var checkAuthTrigger: BehaviorSubject<Int>


    override fun checkAuthIntent(): Observable<Int> {
        return checkAuthTrigger
    }

    override fun reenterIntent(): Observable<CharSequence> {
        return Observable.merge(
            RxTextView.textChanges(etLoginPhone).skipInitialValue(),
            RxTextView.textChanges(etPassword).skipInitialValue()
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
            state.loading -> {
                mainView.isClickable = false
                loading.visibility = View.VISIBLE
            }
            state.errorStateShown -> {
                mainView.isClickable = true
                loading.visibility = View.GONE
                if (state.errorMessage == "Номер не может быть пустым") {
                    layoutTextInputPhone.error = state.errorMessage
                    layoutTextInput.error = " "
                } else {
                    layoutTextInputPhone.error = " "
                    layoutTextInput.error = state.errorMessage
                }
            }
            state.successFullyAuthorized -> {
                mainView.isClickable = true
                loading.visibility = View.GONE
                val intent = Intent (activity, MainPageActivity::class.java)
                activity?.startActivity(intent)
            }
            state.defaultState -> {
                mainView.isClickable = true
                loading.visibility = View.GONE
                layoutTextInputPhone.boxStrokeColor = Color.parseColor("#fa6600")
                layoutTextInput.boxStrokeColor = Color.parseColor("#fa6600")
                layoutTextInputPhone.error = ""
                layoutTextInput.error = ""
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
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
//        val prefs = PreferenceHelper.customPrefs(context!!, Constants.AUTH_PREF_NAME)
//        prefs[Constants.AUTH_TOKEN] = null
        layoutTextInputPhone.boxStrokeColor = Color.parseColor("#fa6600")
        layoutTextInput.boxStrokeColor = Color.parseColor("#fa6600")
        checkAuthTrigger = BehaviorSubject.createDefault(0)
        etLoginPhone.addTextChangedListener(PhoneTextWatcher(etLoginPhone))

        RxView.clicks(tvSendSms).subscribe {
            val navController = NavHostFragment.findNavController(this)
            navController.navigate(R.id.sendSMSFragment)
        }
    }
}
