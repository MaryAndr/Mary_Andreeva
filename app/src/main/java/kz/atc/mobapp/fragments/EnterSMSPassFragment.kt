package kz.atc.mobapp.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_enter_smspass.*

import kz.atc.mobapp.R
import kz.atc.mobapp.presenters.EnterSMSPassPagePresenter
import kz.atc.mobapp.states.EnterSMSPagePartialState
import kz.atc.mobapp.states.EnterSMSPageState
import kz.atc.mobapp.utils.PhoneTextWatcher
import kz.atc.mobapp.views.EnterSMSPassView

class EnterSMSPassFragment : MviFragment<EnterSMSPassView, EnterSMSPassPagePresenter>(),
    EnterSMSPassView {

    lateinit var userName: String
    override fun render(state: EnterSMSPageState) {
        when {
            state.smsResended -> {
                Toast.makeText(context!!, "Sms resended", Toast.LENGTH_SHORT).show()
            }
            state.showTimer -> {
                if (state.countdown == 0.toLong()) {
                    resendSmsTv.setText("Отправить пароль повторно")
                } else {
                    resendSmsTv.setText("Повторно пароль можно отправить через 00:"+ state.countdown.toString())
                }
            }
        }
    }

    override fun createPresenter() = EnterSMSPassPagePresenter(context!!)

    override fun resendSMSIntent(): Observable<String> {
        return RxView.clicks(resendSmsTv).map { userName }
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
        Log.d("debug", userName)
    }
}
