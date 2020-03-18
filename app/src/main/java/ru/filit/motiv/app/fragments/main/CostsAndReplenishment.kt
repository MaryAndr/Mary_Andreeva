package ru.filit.motiv.app.fragments.main

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.NavController
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.fragment_costs_and_replenishment.*
import kotlinx.android.synthetic.main.fragment_costs_and_replenishment.tvPhoneNumber

import ru.filit.motiv.app.R
import ru.filit.motiv.app.adapters.RepAdapter
import ru.filit.motiv.app.presenters.main.CostsAndReplenishmentPresenter
import ru.filit.motiv.app.states.main.CostAndReplenishmentState
import ru.filit.motiv.app.utils.CalendarView
import ru.filit.motiv.app.utils.ConnectivityReceiver
import ru.filit.motiv.app.utils.TextConverter
import ru.filit.motiv.app.utils.TimeUtils
import ru.filit.motiv.app.views.main.CostAndReplenishmentView
import ru.slybeaver.slycalendarview.SlyCalendarDialog


class CostsAndReplenishment :
    MviFragment<CostAndReplenishmentView, CostsAndReplenishmentPresenter>(),
    CostAndReplenishmentView, ConnectivityReceiver.ConnectivityReceiverListener {


    private val connectivityReceiver = ConnectivityReceiver()

    private lateinit var networkAvailabilityTrigger : BehaviorSubject<Boolean>

    override fun checkInternetConnectivityIntent(): Observable<Boolean> {
        return networkAvailabilityTrigger
    }


    override fun getReplenishmentDataIntent(): Observable<String> {

        return showReplenishmentDataTrigger.map<String> {
            tvRepPeriod.text.toString()
        }
    }

    private lateinit var mainDataLoadTrigger: BehaviorSubject<Int>

    private lateinit var showCostsTrigger: BehaviorSubject<Int>

    private lateinit var showReplenishmentTrigger: BehaviorSubject<Int>

    private lateinit var showReplenishmentDataTrigger: BehaviorSubject<Int>

    private var navController: NavController? = null

    override fun mainDataLoadIntent(): Observable<Int> {
        return mainDataLoadTrigger
    }

    override fun showCostsIntent(): Observable<Int> {
        return showCostsTrigger
    }

    override fun showReplenishmentIntent(): Observable<Int> {
        return showReplenishmentTrigger
    }

    override fun render(state: CostAndReplenishmentState) {
        Log.d("dd", "shown $state")

        when {
            state.mainDataLoaded -> renderMainData(state)
            state.costsShown -> {
                costsLayout.visibility = View.VISIBLE
                replenishmentLayout.visibility = View.GONE
            }
            state.replenishmentDataLoaded -> {
                tvRepSum.visibility = View.VISIBLE
                tvRepSum.text =
                    state.replenishmentData!!.sumBy { it.amount.toInt() }.toString() + resources.getString(
                        R.string.rub_value
                    )
                repListView.adapter = RepAdapter(context!!, state.replenishmentData!!)
            }
            state.replenishmentShown -> {
                Log.d("debug", "fixed")
                costsLayout.visibility = View.GONE
                replenishmentLayout.visibility = View.VISIBLE
            }
            state.errorShown -> {
                Log.d("debug", "fixed")
                val dialogBuilder = AlertDialog.Builder(this.context)
                dialogBuilder
                    .setMessage(state.errorText)
                    .setPositiveButton("OK") { _, _ ->
                        if (state.appIsDeprecated){
                            val appPackageName = activity?.packageName

                            try {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=$appPackageName")
                                    )

                                )
                            } catch (anfe: ActivityNotFoundException) {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                                    )
                                )
                            }
                            activity?.finish()
                        }
                    }
                    .setCancelable(false)
                    .create()
                    .show()
                tvRepPeriod.text = TimeUtils().returnPeriodMinusThreeMonth()
            }
            state.loading -> {
                pgMainData.visibility = View.VISIBLE
                tvPhoneNumber.visibility = View.INVISIBLE
                tvBalance.visibility = View.INVISIBLE
                tvTariff.visibility = View.INVISIBLE
            }
            state.connectionLost -> {
                description.visibility = View.GONE
                costsLayout.visibility = View.GONE
                replenishmentLayout.visibility = View.GONE
                costsAndReplenishmentGroup.visibility = View.GONE
                no_internet_view.visibility = View.VISIBLE
            }
            state.connectionResume -> {
                description.visibility = View.VISIBLE
                no_internet_view.visibility = View.GONE
                costsAndReplenishmentGroup.visibility = View.VISIBLE
                mainDataLoadTrigger.onNext(1)
                when(costsAndReplenishmentGroup.checkedRadioButtonId){
                    R.id.costsButton -> {
                        costsLayout.visibility = View.VISIBLE
                        showCostsTrigger.onNext(1)
                    }
                    R.id.replenishmentButton -> {
                        replenishmentLayout.visibility= View.VISIBLE
                        showReplenishmentDataTrigger.onNext(1)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        costsAndReplenishmentGroup.check(R.id.costsButton)

    }

    private fun renderMainData(state: CostAndReplenishmentState) {
        pgMainData.visibility = View.GONE
        tvPhoneNumber.visibility = View.VISIBLE
        tvBalance.visibility = View.VISIBLE
        tvTariff.visibility = View.VISIBLE
        tvDesc.visibility = View.VISIBLE
        tvPhoneNumber.text = TextConverter().getFormattedPhone(state.mainData!!.phoneNumber!!)
        tvBalance.text =
            state.mainData!!.balance.toString() + resources.getString(R.string.rub_value)
        tvTariff.text = "Тариф \"${state.mainData!!.tariffData!!.tariff.name}\""


        if (state.mainData!!.isDetalization) {
            tvDesc.text = getString(R.string.isDetalDescText)
            btOrderDetails.visibility = View.VISIBLE
            btOrderDetails.setOnClickListener {

                val fr = CostsEmailFragment(state.mainData?.phoneNumber!!, state.mainData?.costDetalization!!)
                val fm = fragmentManager
                val fragmentTransaction = fm!!.beginTransaction().addToBackStack("costsAndRep")
                fragmentTransaction.replace(R.id.container, fr)
                fragmentTransaction.commit()
            }
        } else {
            tvDesc.text = getString(R.string.isNotDetalDescText)
            btOrderDetails.visibility = View.GONE
        }
    }

    override fun createPresenter() = CostsAndReplenishmentPresenter(context!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainDataLoadTrigger = BehaviorSubject.create()
        showCostsTrigger = BehaviorSubject.create()
        showReplenishmentTrigger = BehaviorSubject.create()
        showReplenishmentDataTrigger = BehaviorSubject.create()
        networkAvailabilityTrigger = BehaviorSubject.create()
        activity!!.registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.costs)))
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.abs_layout)
            elevation = resources.getDimension(R.dimen.elevation)
            setDisplayHomeAsUpEnabled(false)
        }
        val tvTitle: AppCompatTextView = activity!!.findViewById(R.id.tvTitle)
        tvTitle.setTextColor(resources.getColor(R.color.black))
        tvTitle.text = "Расходы"
        activity!!.nav_view.visibility = View.VISIBLE
        ConnectivityReceiver.connectivityReceiverListener = this
        showCostsTrigger.onNext(1)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_costs_and_replenishment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        costsLayout.visibility = View.GONE
        tvPhoneNumber.visibility = View.INVISIBLE
        tvBalance.visibility = View.INVISIBLE
        tvTariff.visibility = View.INVISIBLE
        tvRepPeriod.text = TimeUtils().returnPeriodMinusThreeMonth()
//        activity!!.nav_view.visibility = View.GONE
        mainDataLoadTrigger.onNext(1)

        costsAndReplenishmentGroup.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = view.findViewById(checkedId)
            if (radio.id == R.id.costsButton) {
                showCostsTrigger.onNext(1)
            } else if (radio.id == R.id.replenishmentButton) {
                Log.d("TRIGGERED", "<<PTHER")
                showReplenishmentTrigger.onNext(1)
                showReplenishmentDataTrigger.onNext(1)
            }
        }

        viewRepCalendar.setOnClickListener {
            SlyCalendarDialog()
                .setSingle(false)
                .setIsCosts(false)
                .setFirstMonday(false)
                .setCallback(CalendarView(tvRepPeriod, showReplenishmentDataTrigger))
                .show(activity!!.supportFragmentManager, "TAG_SLYCALENDAR")
        }
    }
    override fun onStart() {
        super.onStart()
        mainDataLoadTrigger.onNext(1)
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
