package ru.filit.motiv.app.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.NavHostFragment
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_login.*
import ru.filit.motiv.app.MainPageActivity
import ru.filit.motiv.app.R
import ru.filit.motiv.app.models.AuthModel
import ru.filit.motiv.app.presenters.LoginPagePresenter
import ru.filit.motiv.app.states.LoginPageState
import ru.filit.motiv.app.utils.PhoneTextWatcher
import ru.filit.motiv.app.utils.TextConverter
import ru.filit.motiv.app.views.LoginPageView


class LoginFragment : MviFragment<LoginPageView, LoginPagePresenter>(), LoginPageView,
    TextView.OnEditorActionListener {

    private lateinit var checkAuthTrigger: BehaviorSubject<Int>

    private lateinit var login: BehaviorSubject<AuthModel>

    private lateinit var viewTreeObserver: ViewTreeObserver.OnGlobalLayoutListener


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
        return login
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
                scrollToBottom()
                if (state.errorMessage == "Номер не может быть пустым" || state.errorMessage == "Введите корректный номер телефона") {
                    layoutTextInputPhone.error = state.errorMessage
                    layoutTextInput.error = " "
                } else {
                    layoutTextInputPhone.error = " "
                    layoutTextInput.error = state.errorMessage
                }
                layoutTextInputPhone.hint = context?.getString(R.string.phone_number)
            }
            state.successFullyAuthorized -> {
                mainView.isClickable = true
                loading.visibility = View.GONE
                val intent = Intent(activity, MainPageActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity?.startActivity(intent)
            }
            state.defaultState -> {
                mainView.isClickable = true
                loading.visibility = View.GONE
                if (etLoginPhone.text.isNullOrEmpty()) {
                    layoutTextInputPhone.hint = context?.getString(R.string.phone_hint)
                } else {
                    layoutTextInputPhone.hint = context?.getString(R.string.phone_number)
                }
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
        if (!etLoginPhone.text.isNullOrEmpty()) {
            layoutTextInputPhone.hint = context?.getString(R.string.phone_number)
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
//        val prefs = PreferenceHelper.customPrefs(context!!, Constants.AUTH_PREF_NAME)
//        prefs[Constants.AUTH_TOKEN] = null
        layoutTextInputPhone.boxStrokeColor = Color.parseColor("#fa6600")
        layoutTextInput.boxStrokeColor = Color.parseColor("#fa6600")
        login = BehaviorSubject.create()
        checkAuthTrigger = BehaviorSubject.createDefault(0)
        etLoginPhone.addTextChangedListener(PhoneTextWatcher(etLoginPhone))
        etLoginPhone.setOnEditorActionListener(this)
        etPassword.setOnEditorActionListener(this)

        etLoginPhone.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                layoutTextInputPhone.hint = context?.getString(R.string.phone_number)
                scrollToBottom()
            }
        }

        etPassword.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                scrollToBottom()
                if (etLoginPhone.text.isNullOrEmpty() && !layoutTextInputPhone.isErrorEnabled) {
                    layoutTextInputPhone.hint = context?.getString(R.string.phone_hint)
                }
            }
        }


        buttonAuth.setOnClickListener {
            login.onNext(
                AuthModel(
                    TextConverter().getOnlyDigits(etLoginPhone.text.toString()),
                    etPassword.text.toString()
                )
            )
        }

        RxView.clicks(tvSendSms).subscribe {
            val navController = NavHostFragment.findNavController(this)
            navController.navigate(R.id.sendSMSFragment)
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            etPassword.requestFocus()
            return true
        }

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            login.onNext(
                AuthModel(
                    TextConverter().getOnlyDigits(etLoginPhone.text.toString()),
                    etPassword.text.toString()
                )
            )
            return true
        }
        return false
    }

    private fun scrollToBottom() {
        viewTreeObserver = ViewTreeObserver.OnGlobalLayoutListener {
            val scrollViewHeight = scrollView.getHeight()
            if (scrollViewHeight > 0) {
                val lastView =
                    scrollView.getChildAt(scrollView.getChildCount() - 1)
                val lastViewBottom =
                    lastView.getBottom() + scrollView.getPaddingBottom()
                val deltaScrollY =
                    lastViewBottom - scrollViewHeight - scrollView.getScrollY()
                /* If you don't want, call this. */
                scrollView.scrollBy(0, deltaScrollY)
                scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(viewTreeObserver)
            }
        }
        scrollView.getViewTreeObserver()
            .addOnGlobalLayoutListener(viewTreeObserver)
    }
}