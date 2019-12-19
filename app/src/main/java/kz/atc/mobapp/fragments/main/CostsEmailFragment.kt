package kz.atc.mobapp.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
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
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.fragment_costs_email.*

import kz.atc.mobapp.R
import kz.atc.mobapp.models.EmailDetalModel
import kz.atc.mobapp.presenters.main.CostsEmailPresenter
import kz.atc.mobapp.states.main.CostsEmailState
import kz.atc.mobapp.utils.CalendarView
import kz.atc.mobapp.utils.TextConverter
import kz.atc.mobapp.views.main.CostsEmailView
import ru.slybeaver.slycalendarview.SlyCalendarDialog


class CostsEmailFragment :
    MviFragment<CostsEmailView, CostsEmailPresenter>(),
    CostsEmailView  {

    private lateinit var msisdnLoadTrigger: BehaviorSubject<Int>

    override fun msisdnLoadIntent(): Observable<Int> {
        return msisdnLoadTrigger
    }

    override fun sendEmailIntent(): Observable<EmailDetalModel> {
        return RxView.clicks(btnSend).map<EmailDetalModel> {
            EmailDetalModel(etEnterEmail.text.toString(), tvPeriod.text.toString())
        }
    }

    override fun render(state: CostsEmailState) {
        when(state) {
            is CostsEmailState.MsisdnShown -> {
                tvPhoneNumber.text = TextConverter().getFormattedPhone(state.msisdn)
                tvPeriod.text = state.defPeriod
            }
            is CostsEmailState.ErrorShown -> {
                layoutTextInputEnterEmail.error = state.error
            }
        }
    }

    override fun createPresenter() = CostsEmailPresenter(context!!)

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_backbutton_black)
        var tvTitle: AppCompatTextView = activity!!.findViewById(R.id.tvTitle)
        activity!!.nav_view.visibility = View.INVISIBLE
        tvTitle.setTextColor(resources.getColor(R.color.black))
        tvTitle.text = "Заказать детализацию"
        msisdnLoadTrigger.onNext(1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        msisdnLoadTrigger = BehaviorSubject.create()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_costs_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewCalendar.setOnClickListener {
            SlyCalendarDialog()
                .setSingle(false)
                .setFirstMonday(false)
                .setCallback(CalendarView(tvPeriod))
                .show(activity!!.supportFragmentManager, "TAG_SLYCALENDAR")
        }
    }
}
