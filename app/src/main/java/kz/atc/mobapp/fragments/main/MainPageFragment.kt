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
import kz.atc.mobapp.models.main.IndicatorHolder
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
        tvTariffName.text = state.mainData?.tariffData?.tariff?.name
        tvChargeDate.text = TimeUtils().debitDate(state.mainData?.chargeDate!!)
        tvAbonBalance.text =
            "${state.mainData?.balance.toString()} ${resources.getString(R.string.rub_value)}"
        loadBars(state.mainData?.indicatorHolder!!)
    }

    private fun loadBars(indicatorHolder: MutableMap<String, IndicatorHolder>) {
        if (indicatorHolder.containsKey("DATA")) {
            if (!indicatorHolder["DATA"]!!.unlim && indicatorHolder["DATA"]!!.valueUnit == null) {
                dataView.groupData.visibility = View.VISIBLE
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
                tvDataRestAmount.text = "Безлимит"
                tvDataTotalAmount.text = "Интернет"
            } else if (indicatorHolder["DATA"]!!.valueUnit != null) {
                tvDataRestAmount.text = indicatorHolder["DATA"]!!.valueUnit
                tvDataTotalAmount.text = "Интернет"
            }
        }
        if (indicatorHolder.containsKey("VOICE")) {
            dataView.groupPhone.visibility = View.VISIBLE
            var rest = indicatorHolder["VOICE"]?.rest!!
            var total = indicatorHolder["VOICE"]?.total!!


            pbPhone.progress = indicatorHolder["VOICE"]?.percent!!

            tvVoiceRestAmount.text = "$rest Мин"
            tvVoiceTotalAmount.text = "из $total Мин"
        }
        if (indicatorHolder.containsKey("SMS")) {
            dataView.groupSMS.visibility = View.VISIBLE
            var rest = indicatorHolder["SMS"]?.rest!!
            var total = indicatorHolder["SMS"]?.total!!

            pbSms.progress = indicatorHolder["SMS"]?.percent!!

            tvSMSRestAmount.text = "$rest SMS"
            tvSmsTotalAmount.text = "из $total SMS"
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
