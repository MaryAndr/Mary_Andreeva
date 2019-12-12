package kz.atc.mobapp.fragments.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_costs_and_replenishment.*

import kz.atc.mobapp.R
import kz.atc.mobapp.presenters.main.CostsAndReplenishmentPresenter
import kz.atc.mobapp.states.main.CostAndReplenishmentState
import kz.atc.mobapp.views.main.CostAndReplenishmentView


class CostsAndReplenishment :
    MviFragment<CostAndReplenishmentView, CostsAndReplenishmentPresenter>(),
    CostAndReplenishmentView {

    private lateinit var mainDataLoadTrigger: BehaviorSubject<Int>

    override fun mainDataLoadIntent(): Observable<Int> {
        return mainDataLoadTrigger
    }

    override fun showCostsIntent(): Observable<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showReplenishmentIntent(): Observable<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun render(state: CostAndReplenishmentState) {
        when {
            state.mainDataLoaded -> {
                renderMainData(state)
            }
        }
    }

    fun renderMainData(state: CostAndReplenishmentState) {
        tvPhoneNumber.text = state.mainData!!.phoneNumber
        tvBalance.text = state.mainData!!.balance.toString()
        tvTariff.text = state.mainData!!.tariffData!!.tariff.name
    }

    override fun createPresenter() = CostsAndReplenishmentPresenter(context!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainDataLoadTrigger = BehaviorSubject.create()

    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.show()
        var tvTitle: AppCompatTextView = activity!!.findViewById(R.id.tvTitle)
        tvTitle.setTextColor(resources.getColor(R.color.black))
        mainDataLoadTrigger.onNext(1)
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
        costsAndReplenishmentGroup.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = view.findViewById(checkedId)
            Toast.makeText(
                context, " On checked change : ${radio.text}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}
