package kz.atc.mobapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_send_sms.*

import kz.atc.mobapp.R
import kz.atc.mobapp.presenters.SendSMSPagePresenter
import kz.atc.mobapp.states.SendSMSPageState
import kz.atc.mobapp.utils.PhoneTextWatcher
import kz.atc.mobapp.utils.TextConverter
import kz.atc.mobapp.views.SendSMSScreenView


class SendSMSFragment : MviFragment<SendSMSScreenView, SendSMSPagePresenter>(), SendSMSScreenView {

    private lateinit var defaultStateTrigger: BehaviorSubject<Int>

    override fun defaultIntent(): Observable<Int> {
        return defaultStateTrigger
    }

    override fun createPresenter() = SendSMSPagePresenter(context!!)
    private var navController : NavController? = null

    override fun sendSMSButtonIntent(): Observable<String> {
        return RxView.clicks(buttonGetPass).map {
            TextConverter().getOnlyDigits(etSendSMSPhone.text.toString())
        }
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
        (activity as AppCompatActivity).supportActionBar?.show()
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

        return inflater.inflate(R.layout.fragment_send_sms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etSendSMSPhone.addTextChangedListener(PhoneTextWatcher(etSendSMSPhone))
    }
}
