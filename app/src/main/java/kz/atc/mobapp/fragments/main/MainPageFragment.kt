package kz.atc.mobapp.fragments.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_main_page.*
import kotlinx.android.synthetic.main.fragment_main_page.view.*

import kz.atc.mobapp.R
import kz.atc.mobapp.models.main.IndicatorHolder
import kz.atc.mobapp.presenters.main.MainPagePresenter
import kz.atc.mobapp.states.main.MainPageState
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
        imgservices.visibility = View.VISIBLE
        tvservices.visibility = View.VISIBLE

        if(state.mainData?.subExchange?.available!!) {
            imgmin_gb.visibility = View.VISIBLE
            tvmin_gb.visibility = View.VISIBLE
        }
        tvAbonNumber.text = TextConverter().getFormattedPhone(phoneNumber!!)
        tvTariffName.text = state.mainData?.tariffData?.tariff?.name
        tvChargeDate.text = state.mainData?.chargeDate!!
        tvAbonBalance.text =
            "${state.mainData?.balance.toString()} ${resources.getString(R.string.rub_value)}"
        loadBars(state.mainData?.indicatorHolder!!)
    }

    private fun loadBars(indicatorHolder: MutableMap<String, IndicatorHolder>) {
        if (indicatorHolder.containsKey("DATA")) {
            if (!indicatorHolder["DATA"]!!.unlim && indicatorHolder["DATA"]!!.valueUnit == null) {
                dataView.pbInternet.visibility = View.VISIBLE
                dataView.tvDataRestAmount.visibility = View.VISIBLE
                dataView.tvDataTotalAmount.visibility = View.VISIBLE

                var rest = indicatorHolder["DATA"]?.rest!!
                var total = indicatorHolder["DATA"]?.total!!
                pbInternet.progress = indicatorHolder["DATA"]?.percent!!
                tvDataRestAmount.text =
                    "${StringUtils().unitValueConverter(rest).value} ${StringUtils().unitValueConverter(
                        rest
                    ).unit}"
                tvDataTotalAmount.text =
                    "из ${StringUtils().unitValueConverter(total).value} ${StringUtils().unitValueConverter(
                        total
                    ).unit}"
            } else if (indicatorHolder["DATA"]!!.unlim) {
                dataView.pbInternet.visibility = View.INVISIBLE
                dataView.tvDataRestAmount.visibility = View.VISIBLE
                dataView.tvDataTotalAmount.visibility = View.VISIBLE
                tvDataRestAmount.text = "Безлимит"
                tvDataTotalAmount.text = "Интернет"
            } else if (indicatorHolder["DATA"]!!.valueUnit != null) {
                dataView.pbInternet.visibility = View.INVISIBLE
                dataView.tvDataRestAmount.visibility = View.VISIBLE
                dataView.tvDataTotalAmount.visibility = View.VISIBLE
                tvDataRestAmount.text = indicatorHolder["DATA"]!!.valueUnit
                tvDataTotalAmount.text = "Интернет"
            }
        }
        if (indicatorHolder.containsKey("VOICE")) {
            if (!indicatorHolder["VOICE"]!!.unlim && indicatorHolder["VOICE"]!!.valueUnit == null) {
                dataView.pbPhone.visibility = View.VISIBLE
                dataView.tvVoiceRestAmount.visibility = View.VISIBLE
                dataView.tvVoiceTotalAmount.visibility = View.VISIBLE
                var rest = indicatorHolder["VOICE"]?.rest!!
                var total = indicatorHolder["VOICE"]?.total!!
                pbPhone.progress = indicatorHolder["VOICE"]?.percent!!
                tvVoiceRestAmount.text = "$rest Мин"
                tvVoiceTotalAmount.text = "из $total Мин"
            } else if (indicatorHolder["VOICE"]!!.unlim) {
                dataView.pbPhone.visibility = View.INVISIBLE
                dataView.tvVoiceRestAmount.visibility = View.VISIBLE
                dataView.tvVoiceTotalAmount.visibility = View.VISIBLE
                pbPhone.visibility = View.INVISIBLE
                tvVoiceRestAmount.text = "Безлимит"
                tvVoiceTotalAmount.text = "Остальные исходящие"
            } else if (indicatorHolder["VOICE"]!!.valueUnit != null) {
                dataView.pbPhone.visibility = View.INVISIBLE
                dataView.tvVoiceRestAmount.visibility = View.VISIBLE
                dataView.tvVoiceTotalAmount.visibility = View.VISIBLE
                dataView.pbPhone.visibility = View.INVISIBLE
                tvDataRestAmount.text = indicatorHolder["VOICE"]!!.valueUnit
                tvDataTotalAmount.text = "Исходящие звонки"
            }
        }
        if (indicatorHolder.containsKey("SMS")) {
            if (!indicatorHolder["SMS"]!!.unlim && indicatorHolder["SMS"]!!.valueUnit == null) {
                dataView.pbSms.visibility = View.VISIBLE
                dataView.tvSMSRestAmount.visibility = View.VISIBLE
                dataView.tvSmsTotalAmount.visibility = View.VISIBLE
                var rest = indicatorHolder["SMS"]?.rest!!
                var total = indicatorHolder["SMS"]?.total!!
                pbSms.progress = indicatorHolder["SMS"]?.percent!!
                tvSMSRestAmount.text = "$rest SMS"
                tvSmsTotalAmount.text = "из $total SMS"
            } else if (indicatorHolder["SMS"]!!.unlim) {
                dataView.pbSms.visibility = View.INVISIBLE
                dataView.tvSMSRestAmount.visibility = View.VISIBLE
                dataView.tvSmsTotalAmount.visibility = View.VISIBLE
                tvSMSRestAmount.text = "Безлимит"
                tvSmsTotalAmount.text = "SMS"
            } else if (indicatorHolder["SMS"]!!.valueUnit != null) {
                dataView.pbSms.visibility = View.INVISIBLE
                dataView.tvSMSRestAmount.visibility = View.VISIBLE
                dataView.tvSmsTotalAmount.visibility = View.VISIBLE
                tvSMSRestAmount.text = indicatorHolder["SMS"]!!.valueUnit
                tvSmsTotalAmount.text = "SMS"
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
        dataView.pbInternet.visibility = View.INVISIBLE
        dataView.tvDataRestAmount.visibility = View.INVISIBLE
        dataView.tvDataTotalAmount.visibility = View.INVISIBLE

        dataView.pbSms.visibility = View.INVISIBLE
        dataView.tvSMSRestAmount.visibility = View.INVISIBLE
        dataView.tvSmsTotalAmount.visibility = View.INVISIBLE

        dataView.pbPhone.visibility = View.INVISIBLE
        dataView.tvVoiceRestAmount.visibility = View.INVISIBLE
        dataView.tvVoiceTotalAmount.visibility = View.INVISIBLE


        imgmin_gb.visibility = View.INVISIBLE
        tvmin_gb.visibility = View.INVISIBLE

        imgservices.visibility = View.INVISIBLE
        tvservices.visibility = View.INVISIBLE
    }

}
