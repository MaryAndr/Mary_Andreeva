package ru.filit.motiv.app.fragments.main


import android.app.AlertDialog
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.fragment_costs_email.*

import ru.filit.motiv.app.R
import ru.filit.motiv.app.models.EmailDetalModel
import ru.filit.motiv.app.presenters.main.CostsEmailPresenter
import ru.filit.motiv.app.states.main.CostsEmailState
import ru.filit.motiv.app.utils.CalendarView
import ru.filit.motiv.app.utils.ConnectivityReceiver
import ru.filit.motiv.app.utils.TextConverter
import ru.filit.motiv.app.views.main.CostsEmailView
import ru.slybeaver.slycalendarview.SlyCalendarDialog


class CostsEmailFragment :
    MviFragment<CostsEmailView, CostsEmailPresenter>(),
    CostsEmailView , ConnectivityReceiver.ConnectivityReceiverListener {

    private val connectivityReceiver = ConnectivityReceiver()

    private lateinit var networkAvailabilityTrigger : BehaviorSubject<Boolean>

    override fun checkInternetConnectivityIntent(): Observable<Boolean> {
        return networkAvailabilityTrigger
    }

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
                group.visibility = View.VISIBLE
                pgCostEmail.visibility = View.GONE
                tvPhoneNumber.text = TextConverter().getFormattedPhone(state.msisdn)
                tvPeriod.text = state.defPeriod
                costs_service.text = "Стоимость услуги - ${state.costsDetalization.toInt()} руб"
            }
            is CostsEmailState.ErrorShown -> {
                group.visibility = View.VISIBLE
                pgCostEmail.visibility = View.GONE
                layoutTextInputEnterEmail.error = state.error
            }
            is CostsEmailState.Loading -> {
                group.visibility = View.GONE
                pgCostEmail.visibility = View.VISIBLE

            }
            is CostsEmailState.EmailSent ->{
                group.visibility = View.VISIBLE
                pgCostEmail.visibility = View.GONE
                val dialogBuilder = AlertDialog.Builder(this.context)
                dialogBuilder
                    .setMessage("Отчет успешно отправлен")
                    .setPositiveButton("OK") { _, _ ->
                    }
                    .create()
                    .show()
            }
            is CostsEmailState.InternetState -> {
                if (state.active){
                    no_internet_view.visibility = View.GONE
                    group.visibility = View.VISIBLE
                }else{
                    no_internet_view.visibility = View.VISIBLE
                    group.visibility = View.GONE
                }
            }
        }
    }

    override fun createPresenter() = CostsEmailPresenter(context!!)

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
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
        networkAvailabilityTrigger = BehaviorSubject.create()
        activity!!.registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
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

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            networkAvailabilityTrigger.onNext(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(connectivityReceiver)
    }
}
