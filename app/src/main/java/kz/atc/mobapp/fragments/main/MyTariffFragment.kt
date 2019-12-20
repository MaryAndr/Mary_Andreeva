package kz.atc.mobapp.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_my_tariff.*

import kz.atc.mobapp.R
import kz.atc.mobapp.adapters.MyTariffServicesAdapter
import kz.atc.mobapp.adapters.RepAdapter
import kz.atc.mobapp.presenters.main.MyTariffPresenter
import kz.atc.mobapp.states.main.MyTariffState
import kz.atc.mobapp.utils.StringUtils
import kz.atc.mobapp.utils.TextConverter
import kz.atc.mobapp.views.main.MyTariffView

/**
 * A simple [Fragment] subclass.
 */
class MyTariffFragment : MviFragment<MyTariffView, MyTariffPresenter>(),
    MyTariffView {

    override fun createPresenter() = MyTariffPresenter(context!!)

    private lateinit var preLoadTrigger: BehaviorSubject<Int>

    override fun preLoadIntent(): Observable<Int> {
        return preLoadTrigger
    }

    override fun render(state: MyTariffState) {
        when {
            state.mainDataLoaded -> {
                val subTariff = state.mainData?.subscriberTariff
                val catalogTariff = state.mainData?.catalogTariff

                tvTariffName.text = subTariff?.tariff?.name
                if (subTariff?.tariff?.id in mutableListOf(14, 26, 27, 28)) {
                    tvTariffCondition.text = TextConverter().descriptionBuilder(
                        subTariff?.constructor?.min,
                        subTariff?.constructor?.data,
                        subTariff?.constructor?.sms
                    )
                } else {
                    val shortDesc: String? =
                        catalogTariff?.tariffs?.first()?.attributes?.firstOrNull { pred -> pred.system_name == "short_description" }?.value
                    tvTariffCondition.text = shortDesc
                }

                tvTariffRate.text = catalogTariff?.tariffs?.first()?.attributes?.firstOrNull { pred -> pred.system_name == "write_off_period" }?.value
                tvTariffDate.text = subTariff?.charge_date
                addedRecyclerView.layoutManager = LinearLayoutManager(context!!)
                addedRecyclerView.adapter = MyTariffServicesAdapter(state.mainData?.servicesList, context!!)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preLoadTrigger = BehaviorSubject.create()
    }

    override fun onResume() {
        super.onResume()
        preLoadTrigger.onNext(1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_my_tariff, container, false)
    }


}
