package kz.atc.mobapp.fragments.main

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_main_page.*
import kotlinx.android.synthetic.main.fragment_main_page.view.*

import kz.atc.mobapp.R
import kz.atc.mobapp.models.RemainsResponse
import kz.atc.mobapp.presenters.main.MainPagePresenter
import kz.atc.mobapp.states.main.MainPageState
import kz.atc.mobapp.utils.MathUtils
import kz.atc.mobapp.utils.StringUtils
import kz.atc.mobapp.utils.TextConverter
import kz.atc.mobapp.utils.TimeUtils
import kz.atc.mobapp.views.main.MainPageView


class MainPageFragment : MviFragment<MainPageView, MainPagePresenter>(),
    MainPageView {
    override fun createPresenter() = MainPagePresenter(context!!)

    private lateinit var preLoadTrigger: BehaviorSubject<Int>

    override fun preLoadIntent(): Observable<Int> {
        return preLoadTrigger
    }

    override fun render(state: MainPageState) {
        when {
            state.loading -> {
                pgMainData.visibility = View.VISIBLE
                dataView.visibility = View.INVISIBLE
            }
            state.mainDataLoaded -> {
                renderFirstLoad(state)
            }

        }
    }

    private fun renderFirstLoad(state: MainPageState) {
        val phoneNumber = state.mainData?.phoneNumber
        dataView.visibility = View.VISIBLE
        pgMainData.visibility = View.GONE
        tvAbonNumber.text = TextConverter().getFormattedPhone(phoneNumber!!)
        tvTariffName.text = state.mainData?.tariffName
        tvChargeDate.text = TimeUtils().debitDate(state.mainData?.chargeDate!!)
        tvAbonBalance.text = "${state.mainData?.balance.toString()} ${resources.getString(R.string.rub_value)}"
        loadBars(state.mainData?.remains!!)
    }

    private fun loadBars(remains: List<RemainsResponse>) {
        remains.forEach {
            if(it.type == "DATA" && it.services.primary) {
                dataView.groupData.visibility = View.VISIBLE
                var rest = it.rest_amount
                var total = it.total_amount

                pbInternet.progress = MathUtils().calculatePercent(rest,total)

                tvDataRestAmount.text = "${StringUtils().unitValueConverter(rest).value} ${StringUtils().unitValueConverter(rest).unit}"
                tvDataTotalAmount.text =  "из ${StringUtils().unitValueConverter(total).value} ${StringUtils().unitValueConverter(total).unit}"
            }
            if(it.type == "VOICE" && it.services.primary) {
                dataView.groupPhone.visibility = View.VISIBLE
                var rest = it.rest_amount
                var total = it.total_amount


                pbPhone.progress = MathUtils().calculatePercent(rest,total)

                tvVoiceRestAmount.text = "$rest Мин"
                tvVoiceTotalAmount.text = "из $total Мин"
            }
            if(it.type == "SMS" && it.services.primary) {
                dataView.groupSMS.visibility = View.VISIBLE
                var rest = it.rest_amount
                var total = it.total_amount

                pbSms.progress = MathUtils().calculatePercent(rest,total)

                tvSMSRestAmount.text =  "$rest SMS"
                tvSmsTotalAmount.text = "из $total SMS"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preLoadTrigger = BehaviorSubject.create()

    }

    override fun onResume() {
        super.onResume()
        pgMainData.visibility = View.GONE
        preLoadTrigger.onNext(1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataView.groupSMS.visibility = View.INVISIBLE
        dataView.groupData.visibility = View.INVISIBLE
        dataView.groupPhone.visibility = View.INVISIBLE
    }

}
