package kz.atc.mobapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_enter_smspass.*
import kz.atc.mobapp.R
import kz.atc.mobapp.models.AuthModel
import kz.atc.mobapp.presenters.EnterSMSPassPagePresenter
import kz.atc.mobapp.states.EnterSMSPageState
import kz.atc.mobapp.utils.TextConverter
import kz.atc.mobapp.utils.TimeUtils
import kz.atc.mobapp.views.EnterSMSPassView
import java.util.concurrent.TimeUnit

class EnterSMSPassFragment : MviFragment<EnterSMSPassView, EnterSMSPassPagePresenter>(),
    EnterSMSPassView {

    private lateinit var firstAttemptTrigger: BehaviorSubject<Int>

    override fun firstAttemptIntent(): Observable<Int> {
        return firstAttemptTrigger
    }

    override fun authorizeIntent(): Observable<AuthModel> {
        return RxView.clicks(buttonEnterSms).map<AuthModel> {
            AuthModel(TextConverter().getOnlyDigits(userName), etEnterPassSms.text.toString())
        }
    }

    private lateinit var userName: String

    override fun render(state: EnterSMSPageState) {
        when {
            state.smsResended -> {
                Toast.makeText(context!!, "Sms resended", Toast.LENGTH_SHORT).show()
            }
            state.autorize -> {
                Log.d("Auth intent", "TRIGGERED")
                Toast.makeText(context!!, "Authorized", Toast.LENGTH_SHORT).show()
                state.autorize = false
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
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.show()

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

        Log.d("debug", userName)
    }
}
