package kz.atc.mobapp.fragments.main


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_my_tariff.*

import kz.atc.mobapp.R
import kz.atc.mobapp.adapters.MyTariffServicesAdapter
import kz.atc.mobapp.adapters.RepAdapter
import kz.atc.mobapp.dialogs.MyTariffAboutDialog
import kz.atc.mobapp.models.main.IndicatorHolder
import kz.atc.mobapp.models.main.MyTariffAboutData
import kz.atc.mobapp.presenters.main.MyTariffPresenter
import kz.atc.mobapp.states.main.MyTariffState
import kz.atc.mobapp.utils.StringUtils
import kz.atc.mobapp.utils.TextConverter
import kz.atc.mobapp.utils.TimeUtils
import kz.atc.mobapp.views.main.MyTariffView

/**
 * A simple [Fragment] subclass.
 */
class MyTariffFragment : MviFragment<MyTariffView, MyTariffPresenter>(),
    MyTariffView {

    override fun createPresenter() = MyTariffPresenter(context!!)

    private lateinit var preLoadTrigger: BehaviorSubject<Int>


    private lateinit var aboutData: MyTariffAboutData

    override fun preLoadIntent(): Observable<Int> {
        return preLoadTrigger
    }


    override fun render(state: MyTariffState) {
        when {
            state.mainDataLoaded -> {
                val subTariff = state.mainData?.subscriberTariff
                val catalogTariff = state.mainData?.catalogTariff


                aboutData = MyTariffAboutData(subTariff, catalogTariff)
                tvTariffName.text = subTariff?.tariff?.name
                if (subTariff?.tariff?.id in mutableListOf(14, 26, 27, 28)) {
                    tvTariffCondition.text = TextConverter().descriptionBuilder(
                        subTariff?.constructor?.min,
                        subTariff?.constructor?.data,
                        subTariff?.constructor?.sms
                    )
                } else {
                    val shortDesc: String? =
                        catalogTariff?.tariffs?.first()
                            ?.attributes?.firstOrNull { pred -> pred.system_name == "short_description" }
                            ?.value
                    tvTariffCondition.text = shortDesc
                }

                tvTariffRate.text = catalogTariff?.tariffs?.first()
                    ?.attributes?.firstOrNull { pred -> pred.system_name == "write_off_period" }
                    ?.value
                tvTariffDate.text = subTariff?.charge_date
                addedRecyclerView.layoutManager = LinearLayoutManager(context!!)
                addedRecyclerView.adapter =
                    MyTariffServicesAdapter(state.mainData?.servicesList, context!!)

                renderIndicators(state.mainData?.indicatorHolder, subTariff?.charge_date)
            }
        }
    }

    private fun renderIndicators(
        indicatorHolder: Map<String, IndicatorHolder>?,
        chargeDate: String?
    ) {
        if (indicatorHolder?.containsKey("DATA")!!) {
            viewAmountInetConditions.visibility = View.VISIBLE
            val rest = indicatorHolder["DATA"]?.rest!!
            val total = indicatorHolder["DATA"]?.total!!
            tvInetConditionsRest.text =
                "${StringUtils().unitValueConverter(rest).value} ${StringUtils().unitValueConverter(
                    rest
                ).unit}"
            tvInetConditionsTotal.text =
                "из ${StringUtils().unitValueConverter(total).value} ${StringUtils().unitValueConverter(
                    total
                ).unit}"
            if (chargeDate == null) {
                tvInetConditionsDate.visibility = View.GONE
            } else {
                tvInetConditionsDate.text =
                    "до ${TimeUtils().changeFormat(chargeDate, "yyyy-MM-dd", "dd.MM.yyyy")}"
            }
            pbInetConditions.progress = indicatorHolder["DATA"]?.percent!!
        }
        if (indicatorHolder?.containsKey("VOICE")!!) {
            constVoice.visibility = View.VISIBLE
            val rest = indicatorHolder["VOICE"]?.rest!!
            val total = indicatorHolder["VOICE"]?.total!!
            tvVoiceConditionsRest.text =
                "${StringUtils().unitValueConverter(rest).value} ${StringUtils().unitValueConverter(
                    rest
                ).unit}"
            tvVoiceConditionsTotal.text =
                "из ${StringUtils().unitValueConverter(total).value} ${StringUtils().unitValueConverter(
                    total
                ).unit}"
            if (chargeDate == null) {
                tvVoiceConditionsDate.visibility = View.GONE
            } else {
                tvVoiceConditionsDate.text =
                    "до ${TimeUtils().changeFormat(chargeDate, "yyyy-MM-dd", "dd.MM.yyyy")}"
            }
            pbVoiceConditions.progress = indicatorHolder["VOICE"]?.percent!!
        }
        if (indicatorHolder?.containsKey("SMS")!!) {
            constSms.visibility = View.VISIBLE
            val rest = indicatorHolder["SMS"]?.rest!!
            val total = indicatorHolder["SMS"]?.total!!
            tvSmsConditionsRest.text =
                "${StringUtils().unitValueConverter(rest).value} ${StringUtils().unitValueConverter(
                    rest
                ).unit}"
            tvSmsConditionsTotal.text =
                "из ${StringUtils().unitValueConverter(total).value} ${StringUtils().unitValueConverter(
                    total
                ).unit}"
            if (chargeDate == null) {
                tvSmsConditionsDate.visibility = View.GONE
            } else {
                tvSmsConditionsDate.text =
                    "до ${TimeUtils().changeFormat(chargeDate, "yyyy-MM-dd", "dd.MM.yyyy")}"
            }
            Log.d("SMS INDICATOR", indicatorHolder["SMS"]?.percent!!.toString())
            pbSmsConditions.progress = indicatorHolder["SMS"]?.percent!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RxJavaPlugins.setErrorHandler { throwable ->
            throwable.printStackTrace()
        }
        preLoadTrigger = BehaviorSubject.create()
    }

    override fun onResume() {
        super.onResume()
        preLoadTrigger.onNext(1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvAboutTariff.setOnClickListener {
            if (::aboutData.isInitialized) {
                val aboutDialog = MyTariffAboutDialog.newInstance(aboutData)
                aboutDialog.show(
                    activity!!.getSupportFragmentManager(),
                    "my_tariff_dialog_fragment"
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_my_tariff, container, false)
    }


}
