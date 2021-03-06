package ru.filit.motiv.app.fragments.main


import android.app.AlertDialog
import android.content.IntentFilter
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.ActionBar
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
import ru.filit.motiv.app.utils.hideKeyboard
import ru.filit.motiv.app.views.main.CostsEmailView
import ru.slybeaver.slycalendarview.SlyCalendarDialog
import java.text.NumberFormat


class CostsEmailFragment (private val phoneNumber: String, private val costDetalization: Double) :
    MviFragment<CostsEmailView, CostsEmailPresenter>(),
    CostsEmailView , ConnectivityReceiver.ConnectivityReceiverListener {

    private val connectivityReceiver = ConnectivityReceiver()

    private lateinit var networkAvailabilityTrigger : BehaviorSubject<Boolean>

    private lateinit var sendEmailTrigger: BehaviorSubject<EmailDetalModel>

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
                tvPhoneNumber.text = TextConverter().getFormattedPhone(phoneNumber)
                tvPeriod.text = state.defPeriod
                costs_service.text = "Стоимость услуги - ${NumberFormat.getInstance().format(costDetalization)} руб"
            }

            is CostsEmailState.ErrorShown -> {
                group.visibility = View.VISIBLE
                pgCostEmail.visibility = View.GONE
                layoutTextInputEnterEmail.error = state.error
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
        (activity as AppCompatActivity).supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.costs)))
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.abs_layout)
            elevation = resources.getDimension(R.dimen.elevation)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_backbutton_black)
        }
        val tvTitle: AppCompatTextView = activity!!.findViewById(R.id.tvTitle)
        tvTitle.setTextColor(resources.getColor(R.color.black))
        tvTitle.text = "Заказать детализацию"
        activity!!.nav_view.visibility = View.INVISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        msisdnLoadTrigger = BehaviorSubject.create()
        networkAvailabilityTrigger = BehaviorSubject.create()
        msisdnLoadTrigger.onNext(1)
        sendEmailTrigger = BehaviorSubject.create()
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
        btnSend.setOnClickListener{sendEmailTrigger.onNext(EmailDetalModel(etEnterEmail.text.toString(), tvPeriod.text.toString()))}
        etEnterEmail.setOnEditorActionListener{ v: TextView?, actionId: Int?, keyEvent: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                sendEmailTrigger.onNext(EmailDetalModel(etEnterEmail.text.toString(), tvPeriod.text.toString()))
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
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

    private fun setVisibility(visibility: Int) {
        costs_service.visibility = visibility
        btnSend.visibility = visibility
        layoutTextInputEnterEmail.visibility = visibility
        view2.visibility = visibility
        viewCalendar.visibility = visibility
        textView7.visibility = visibility
        tvPhoneNumber.visibility = visibility
    }
}
