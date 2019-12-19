package kz.atc.mobapp.fragments.main


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.fragment_costs_and_replenishment.*
import kotlinx.android.synthetic.main.fragment_costs_and_replenishment.tvPhoneNumber
import kotlinx.android.synthetic.main.fragment_costs_email.*

import kz.atc.mobapp.R
import kz.atc.mobapp.adapters.RepAdapter
import kz.atc.mobapp.presenters.main.CostsAndReplenishmentPresenter
import kz.atc.mobapp.states.main.CostAndReplenishmentState
import kz.atc.mobapp.utils.CalendarView
import kz.atc.mobapp.utils.TextConverter
import kz.atc.mobapp.utils.TimeUtils
import kz.atc.mobapp.views.main.CostAndReplenishmentView
import ru.slybeaver.slycalendarview.SlyCalendarDialog


class CostsAndReplenishment :
    MviFragment<CostAndReplenishmentView, CostsAndReplenishmentPresenter>(),
    CostAndReplenishmentView {
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
                Toast.makeText(context, state.errorText, Toast.LENGTH_LONG).show()
                tvRepPeriod.text = TimeUtils().returnPeriodMinusThreeMonth()
            }
            state.loading -> {
                pgMainData.visibility = View.VISIBLE
                tvPhoneNumber.visibility = View.INVISIBLE
                tvBalance.visibility = View.INVISIBLE
                tvTariff.visibility = View.INVISIBLE
            }
        }
    }

    override fun onPause() {
        super.onPause()
        costsAndReplenishmentGroup.check(R.id.costsButton)

    }

    private fun renderMainData(state: CostAndReplenishmentState) {
        pgMainData.visibility = View.INVISIBLE
        tvPhoneNumber.visibility = View.VISIBLE
        tvBalance.visibility = View.VISIBLE
        tvTariff.visibility = View.VISIBLE
        tvPhoneNumber.text = TextConverter().getFormattedPhone(state.mainData!!.phoneNumber!!)
        tvBalance.text =
            state.mainData!!.balance.toString() + resources.getString(R.string.rub_value)
        tvTariff.text = "Тариф \"${state.mainData!!.tariffData!!.tariff.name}\""
    }

    override fun createPresenter() = CostsAndReplenishmentPresenter(context!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainDataLoadTrigger = BehaviorSubject.create()
        showCostsTrigger = BehaviorSubject.create()
        showReplenishmentTrigger = BehaviorSubject.create()
        showReplenishmentDataTrigger = BehaviorSubject.create()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        activity!!.nav_view.visibility = View.VISIBLE
        showCostsTrigger.onNext(1)
        var tvTitle: AppCompatTextView = activity!!.findViewById(R.id.tvTitle)
        tvTitle.setTextColor(resources.getColor(R.color.black))
        tvTitle.text = "Расходы"

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
        btOrderDetails.setOnClickListener {

            val fr = CostsEmailFragment()
            val fm = fragmentManager
            val fragmentTransaction = fm!!.beginTransaction().addToBackStack("costsAndRep")
            fragmentTransaction.replace(R.id.container, fr)
            fragmentTransaction.commit()
//            SlyCalendarDialog()
//                .setSingle(false)
//                .setFirstMonday(false)
//                .setCallback(CalendarView())
//                .show(activity!!.supportFragmentManager, "TAG_SLYCALENDAR")
        }

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


}
