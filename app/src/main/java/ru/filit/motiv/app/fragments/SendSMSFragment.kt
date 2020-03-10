package ru.filit.motiv.app.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_send_sms.*
import ru.filit.motiv.app.R
import ru.filit.motiv.app.presenters.SendSMSPagePresenter
import ru.filit.motiv.app.states.SendSMSPageState
import ru.filit.motiv.app.utils.PhoneTextWatcher
import ru.filit.motiv.app.utils.TextConverter
import ru.filit.motiv.app.utils.hideKeyboard
import ru.filit.motiv.app.views.SendSMSScreenView


class SendSMSFragment : MviFragment<SendSMSScreenView, SendSMSPagePresenter>(), SendSMSScreenView {

    private lateinit var defaultStateTrigger: BehaviorSubject<Int>

    private lateinit var sendSMSTrigger: BehaviorSubject<String>

    override fun defaultIntent(): Observable<Int> {
        return defaultStateTrigger
    }

    override fun createPresenter() = SendSMSPagePresenter(context!!)

    private var navController : NavController? = null

    override fun sendSMSButtonIntent(): Observable<String> {
        return sendSMSTrigger
    }

    override fun render(state: SendSMSPageState) {
        when(state) {
            is SendSMSPageState.ErrorState -> {
                layoutTextInputSendSMSPhone.error = state.error
            }
            is SendSMSPageState.SmsSend -> {
                val bundle = Bundle()
                bundle.putString("username",etSendSMSPhone.text.toString())
                navController = NavHostFragment.findNavController(this)
                navController!!.navigate(R.id.enterSMSPassFragment, bundle)
            }
            is SendSMSPageState.DefaultState -> {
            }
        }
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

    override fun onPause() {
        defaultStateTrigger.onNext(1)

        super.onPause()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        defaultStateTrigger =  BehaviorSubject.createDefault(0)
        sendSMSTrigger = BehaviorSubject.create()

        return inflater.inflate(R.layout.fragment_send_sms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etSendSMSPhone.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                layoutTextInputSendSMSPhone.hint = context?.getString(R.string.phone_number)
            }
        }
        etSendSMSPhone.addTextChangedListener(PhoneTextWatcher(etSendSMSPhone))
        etSendSMSPhone.setOnEditorActionListener{v:TextView?, actionId: Int?, keyEvent: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                sendSMSTrigger.onNext(TextConverter().getOnlyDigits(etSendSMSPhone.text.toString()))
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }
        buttonGetPass.setOnClickListener{sendSMSTrigger.onNext(TextConverter().getOnlyDigits(etSendSMSPhone.text.toString()))}
    }
}
