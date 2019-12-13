package kz.atc.mobapp.fragments.main

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
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
        layoutServices.visibility = View.VISIBLE

        if (state.mainData?.subExchange?.available!!) {
            layoutGbToMin.visibility = View.VISIBLE
        }
        tvAbonNumber.text = TextConverter().getFormattedPhone(phoneNumber!!)
        if (state.mainData?.tariffData?.tariff?.name != null) {
            tvTariffName.text = "Тариф \"${state.mainData?.tariffData?.tariff?.name}\""
        } else {
            tvTariffName.text = state.mainData?.tariffData?.tariff?.name
        }
        tvChargeDate.text = state.mainData?.chargeDate!!
        tvAbonBalance.text =
            "${state.mainData?.balance.toString()} ${resources.getString(R.string.rub_value)}"
        loadBars(state.mainData?.indicatorHolder!!)
    }

    private fun loadBars(indicatorHolder: MutableMap<String, IndicatorHolder>) {
        if (indicatorHolder.containsKey("DATA")) {
            if (!indicatorHolder["DATA"]!!.unlim && indicatorHolder["DATA"]!!.valueUnit == null) {

                var rest = indicatorHolder["DATA"]?.rest!!
                var total = indicatorHolder["DATA"]?.total!!

                if (rest != 0 && total != 0) {
                    groupData.visibility = View.VISIBLE

                    pbInternet.progress = indicatorHolder["DATA"]?.percent!!
                    tvDataRestAmount.text =
                        "${StringUtils().unitValueConverter(rest).value} ${StringUtils().unitValueConverter(
                            rest
                        ).unit}"
                    tvDataTotalAmount.text =
                        "из ${StringUtils().unitValueConverter(total).value} ${StringUtils().unitValueConverter(
                            total
                        ).unit}"
                }
            } else if (indicatorHolder["DATA"]!!.unlim) {
                groupData.visibility = View.VISIBLE
                dataView.pbInternet.visibility = View.GONE
                dataView.layoutParams.height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    240f,
                    resources.displayMetrics
                ).toInt()
                tvDataRestAmount.text = "Безлимит"
                tvDataTotalAmount.text = indicatorHolder["DATA"]?.optionsName
            } else if (indicatorHolder["DATA"]!!.valueUnit != null) {
                groupData.visibility = View.VISIBLE
                dataView.pbInternet.visibility = View.GONE
                dataView.layoutParams.height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    240f,
                    resources.displayMetrics
                ).toInt()
                tvDataRestAmount.text = indicatorHolder["DATA"]!!.valueUnit
                tvDataTotalAmount.text = "Интернет"
            }
        }
        if (indicatorHolder.containsKey("VOICE")) {
            Log.d("HERE", "VOICES")
            if (!indicatorHolder["VOICE"]!!.unlim && indicatorHolder["VOICE"]!!.valueUnit == null) {
                var rest = indicatorHolder["VOICE"]?.rest!!
                var total = indicatorHolder["VOICE"]?.total!!

                if (rest != 0 && total != 0) {
                    groupVoice.visibility = View.VISIBLE
                    pbPhone.progress = indicatorHolder["VOICE"]?.percent!!
                    tvVoiceRestAmount.text = "$rest Мин"
                    tvVoiceTotalAmount.text = "из $total Мин"
                }
            } else if (indicatorHolder["VOICE"]!!.unlim) {
                groupVoice.visibility = View.VISIBLE
                dataView.pbPhone.visibility = View.GONE
                dataView.layoutParams.height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    240f,
                    resources.displayMetrics
                ).toInt()
                tvVoiceRestAmount.text = "Безлимит"
                tvVoiceTotalAmount.text = indicatorHolder["VOICE"]?.optionsName
            } else if (indicatorHolder["VOICE"]!!.valueUnit != null) {
                groupVoice.visibility = View.VISIBLE
                dataView.pbPhone.visibility = View.GONE
                dataView.layoutParams.height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    240f,
                    resources.displayMetrics
                ).toInt()
                tvVoiceRestAmount.text = indicatorHolder["VOICE"]!!.valueUnit
                tvVoiceTotalAmount.text = "Исходящие звонки"
            }
        }
        if (indicatorHolder.containsKey("SMS")) {
            if (!indicatorHolder["SMS"]!!.unlim && indicatorHolder["SMS"]!!.valueUnit == null) {
                var rest = indicatorHolder["SMS"]?.rest!!
                var total = indicatorHolder["SMS"]?.total!!

                if (rest != 0 && total != 0) {
                    groupSMS.visibility = View.VISIBLE
                    pbSms.progress = indicatorHolder["SMS"]?.percent!!
                    tvSMSRestAmount.text = "$rest SMS"
                    tvSmsTotalAmount.text = "из $total SMS"
                }
            } else if (indicatorHolder["SMS"]!!.unlim) {
                groupSMS.visibility = View.VISIBLE
                dataView.pbSms.visibility = View.GONE
                dataView.layoutParams.height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    240f,
                    resources.displayMetrics
                ).toInt()
                tvSMSRestAmount.text = "Безлимит"
                tvSmsTotalAmount.text = indicatorHolder["SMS"]?.optionsName
            } else if (indicatorHolder["SMS"]!!.valueUnit != null) {
                groupSMS.visibility = View.VISIBLE
                dataView.pbSms.visibility = View.GONE
                dataView.layoutParams.height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    240f,
                    resources.displayMetrics
                ).toInt()
                tvSMSRestAmount.text = indicatorHolder["SMS"]!!.valueUnit
                tvSmsTotalAmount.text = "SMS"
            }
        }
        if (indicatorHolder.isEmpty()) {
            dataView.layoutParams.height = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                180f,
                resources.displayMetrics
            ).toInt()
            dataView.setBackgroundDrawable(resources.getDrawable(R.drawable.small_path))
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
//        dataView.pbInternet.visibility = View.INVISIBLE
//        dataView.tvDataRestAmount.visibility = View.INVISIBLE
//        dataView.tvDataTotalAmount.visibility = View.INVISIBLE
//
//        dataView.pbSms.visibility = View.INVISIBLE
//        dataView.tvSMSRestAmount.visibility = View.INVISIBLE
//        dataView.tvSmsTotalAmount.visibility = View.INVISIBLE
//
//        dataView.pbPhone.visibility = View.INVISIBLE
//        dataView.tvVoiceRestAmount.visibility = View.INVISIBLE
//        dataView.tvVoiceTotalAmount.visibility = View.INVISIBLE

        groupData.visibility = View.INVISIBLE
        groupVoice.visibility = View.INVISIBLE
        groupSMS.visibility = View.INVISIBLE
        layoutGbToMin.visibility = View.GONE
        layoutServices.visibility = View.GONE
    }

}
