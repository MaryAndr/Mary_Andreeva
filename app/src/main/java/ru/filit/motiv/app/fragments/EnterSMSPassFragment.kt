package ru.filit.motiv.app.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_enter_smspass.*
import ru.filit.motiv.app.MainPageActivity
import ru.filit.motiv.app.R
import ru.filit.motiv.app.models.AuthModel
import ru.filit.motiv.app.presenters.EnterSMSPassPagePresenter
import ru.filit.motiv.app.states.EnterSMSPageState
import ru.filit.motiv.app.utils.TextConverter
import ru.filit.motiv.app.utils.TimeUtils
import ru.filit.motiv.app.utils.hideKeyboard
import ru.filit.motiv.app.views.EnterSMSPassView
import java.util.concurrent.TimeUnit

class EnterSMSPassFragment : MviFragment<EnterSMSPassView, EnterSMSPassPagePresenter>(),
    EnterSMSPassView {

    private lateinit var firstAttemptTrigger: BehaviorSubject<Int>

    private lateinit var authorizationTrigger: BehaviorSubject<AuthModel>

    override fun firstAttemptIntent(): Observable<Int> {
        return firstAttemptTrigger
    }

    override fun authorizeIntent(): Observable<AuthModel> {
        return authorizationTrigger
    }

    private lateinit var userName: String

    override fun render(state: EnterSMSPageState) {
        when {
            state.smsResended -> {
                val dialogBuilder = android.app.AlertDialog.Builder(this.context)
                dialogBuilder
                    .setMessage(state.errorMessage)
                    .setPositiveButton("OK") { _, _ ->
                    }
                    .create()
                    .show()
            }
            state.showError -> {
//                errorDialog(state.errorMessage!!)
                layoutTextInputEnterPassSms.error = state.errorMessage
            }
            state.autorize -> {
                layoutTextInputEnterPassSms.error = ""
                Log.d("Auth intent", "TRIGGERED")
                val intent = Intent (activity, MainPageActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity?.startActivity(intent)
            }
            state.showTimer -> {
                when {
                    state.countdown == 0.toLong() -> {
                        resendSmsTv.isClickable = true
                        resendSmsTv.text = "Отправить пароль повторно"
                        resendSmsTv.setTextColor(Color.parseColor("#FA6600"))
                    }
                    else -> {
                        resendSmsTv.text =
                            "Повторно пароль можно отправить через " + TimeUtils().secondsToString(
                                state.countdown!!
                            )
                        resendSmsTv.setTextColor(Color.parseColor("#919196"))

                        resendSmsTv.isClickable = false

                    }
                }
            }

        }
    }

    override fun createPresenter() = EnterSMSPassPagePresenter(context!!)

    override fun resendSMSIntent(): Observable<String> {
        return RxView.clicks(resendSmsTv).throttleFirst(5, TimeUnit.SECONDS).map { userName }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        firstAttemptTrigger = BehaviorSubject.createDefault(0)
        authorizationTrigger = BehaviorSubject.create()
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.apply {
            elevation = resources.getDimension(R.dimen.elevation)
            show()
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_backbutton)
        }

        var tvTitle: AppCompatTextView = activity!!.findViewById(R.id.tvTitle)
        tvTitle.text = "Вход по паролю из SMS"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_enter_smspass, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userName = arguments!!.getString("username")
        tvPhoneSmsSend.text = "Пароль отправлен на номер $userName"
        buttonEnterSms.setOnClickListener { authorizationTrigger.onNext(AuthModel(TextConverter().getOnlyDigits(userName), etEnterPassSms.text.toString())) }
        etEnterPassSms.setOnEditorActionListener{ v: TextView?, actionId: Int?, keyEvent: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                authorizationTrigger.onNext(AuthModel(TextConverter().getOnlyDigits(userName), etEnterPassSms.text.toString()))
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }

        Log.d("debug", userName)
    }

    private fun errorDialog(errorMessage: String) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Что-то пошло не так")
        builder.setMessage("Обратитесь в службу поддержки по телефону " +
                "8 800 555-33-77")
        builder.setNegativeButton("Ok") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }
}
