package ru.filit.motiv.app.fragments.main


import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.fragment_my_tariff.*

import ru.filit.motiv.app.R
import ru.filit.motiv.app.adapters.IndicatorsAdapter
import ru.filit.motiv.app.adapters.MyTariffServicesAdapter
import ru.filit.motiv.app.dialogs.MyTariffAboutDialog
import ru.filit.motiv.app.models.main.IndicatorsModel
import ru.filit.motiv.app.models.main.MyTariffAboutData
import ru.filit.motiv.app.presenters.main.MyTariffPresenter
import ru.filit.motiv.app.states.main.MyTariffState
import ru.filit.motiv.app.utils.TextConverter
import ru.filit.motiv.app.utils.TimeUtils
import ru.filit.motiv.app.views.main.MyTariffView

class MyTariffFragment : MviFragment<MyTariffView, MyTariffPresenter>(),
    MyTariffView {

    override fun createPresenter() = MyTariffPresenter(context!!)

    private lateinit var preLoadTrigger: BehaviorSubject<Int>


    override fun preLoadIntent(): Observable<Int> {
        return preLoadTrigger
    }

    override fun render(state: MyTariffState) {
        when {
            state.loading -> {
                Log.d("loading", "triggered")
                mainConstraint.visibility = View.GONE
                pgMainData.visibility = View.VISIBLE
            }
            state.mainDataLoaded -> {
                pgMainData.visibility = View.GONE
                mainConstraint.visibility = View.VISIBLE

                if (state.mainData?.exchangeInfo?.available != null && state.mainData?.exchangeInfo?.available!!) {
                    viewEx.visibility = View.VISIBLE
                    viewEx.setOnClickListener {
                        val fr = MinToGbFragment(state.mainData?.exchangeInfo)
                        val fm = fragmentManager
                        val fragmentTransaction = fm!!.beginTransaction().addToBackStack("mytariff")
                        fragmentTransaction.replace(R.id.container, fr)
                        fragmentTransaction.commit()
                    }
                }
                viewOtherTarrifs.setOnClickListener {
                    val fr = ChangeTariff()
                    val fm = fragmentManager
                    val fragmentTransaction = fm!!.beginTransaction().addToBackStack("mytariff")
                    fragmentTransaction.replace(R.id.container, fr)
                    fragmentTransaction.commit()
                }
                viewOtherServices.setOnClickListener {
                    val fr = ServicesFragment()
                    val fm = fragmentManager
                    val fragmentTransaction = fm!!.beginTransaction().addToBackStack("mytariff")
                    fragmentTransaction.replace(R.id.container, fr)
                    fragmentTransaction.commit()
                }

                if (state.mainData?.indicatorModels?.dataIndicators?.size == 0 &&
                    state.mainData?.indicatorModels?.voiceIndicators?.size == 0 &&
                    state.mainData?.indicatorModels?.smsIndicators?.size == 0
                ) {
                    tvLeftoversTitle.visibility = View.GONE
                }
                renderMainData(state)
                mainDataHolder.visibility = View.VISIBLE
            }
            state.errorShown -> {
                Toast.makeText(context, state.errorText, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pgMainData.visibility = View.GONE
    }


    private fun renderMainData(state: MyTariffState) {
        val subTariff = state.mainData?.subscriberTariff
        val catalogTariff = state.mainData?.catalogTariff
        val subService = state.mainData?.servicesListOriginal
        val isSubFee =
            catalogTariff?.tariffs?.first()?.attributes?.firstOrNull { it.system_name == "subscription_fee" }?.value != "0"
        var subFee = ""
        var period = ""
        val aboutData = MyTariffAboutData(subTariff, catalogTariff, subService)

        tvAboutTariff.setOnClickListener {
            val aboutDialog = MyTariffAboutDialog.newInstance(aboutData)
            aboutDialog.show(
                activity!!.getSupportFragmentManager(),
                "my_tariff_dialog_fragment"
            )
        }


        tvTariffName.text = "\"" + subTariff?.tariff?.name + "\""
        if (subTariff?.tariff?.id in mutableListOf(14, 26, 27, 28)) {
            tvTariffCondition.text = TextConverter().descriptionBuilder(
                subTariff?.tariff?.constructor?.min!!.substringBefore(","),
                subTariff?.tariff?.constructor?.data,
                subTariff?.tariff?.constructor?.sms!!.substringBefore(",")
            )

        } else {
            val shortDesc: String? =
                catalogTariff?.tariffs?.first()
                    ?.attributes?.firstOrNull { pred -> pred.system_name == "short_description" }
                    ?.value
            tvTariffCondition.text = shortDesc
        }

        if (subTariff?.tariff?.id !in mutableListOf(14, 26, 27, 28)) {
            subFee = catalogTariff?.tariffs?.first()
                ?.attributes?.firstOrNull { it.system_name == "subscription_fee" }
                ?.value.toString()
        } else {
            subFee = subTariff?.tariff?.constructor?.abon.toString()
        }

        if (!subFee.isNullOrEmpty()) {
            if (catalogTariff?.tariffs?.first()
                    ?.attributes?.firstOrNull { pred -> pred.system_name == "write_off_period" }
                    ?.value == "Ежемесячно"
            ) {
                period = " ${resources.getString(R.string.rub_value)}/месяц"
            } else if (catalogTariff?.tariffs?.first()
                    ?.attributes?.firstOrNull { pred -> pred.system_name == "write_off_period" }
                    ?.value == "Посуточно"
            ) {
                period = " ${resources.getString(R.string.rub_value)}/сутки"
            }
            if (subTariff?.tariff?.constructor?.abon_discount != null && subTariff?.tariff?.constructor?.abon_discount != "0") {
                tvAbonDiscount.visibility = View.VISIBLE
                tvTariffRate.apply {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    text = subFee + period
                }
                tvAbonDiscount.text = subTariff?.tariff?.constructor?.abon_discount + period
            } else {
                tvTariffRate.text = subFee + period
            }
        }

        if (subTariff?.charge_date != null) {
            tvTariffDate.text = "Списание ${TimeUtils().changeFormat(
                subTariff?.charge_date!!,
                "yyyy-MM-dd",
                "dd.MM.yyyy"
            )}"
        } else {
            tvTariffDate.visibility = View.GONE
        }
        addedRecyclerView.layoutManager = LinearLayoutManager(context!!)
        Log.d("tag", "service size: " + state.mainData?.servicesList!!.size)
        addedRecyclerView.adapter =
            MyTariffServicesAdapter(state.mainData?.servicesList, context!!)

        renderNewIndicators(state.mainData?.indicatorModels!!)
//                renderIndicators(state.mainData?.indicatorHolder, subTariff?.charge_date)
    }

    private fun renderNewIndicators(indicatorsModel: IndicatorsModel) {
        if (indicatorsModel.dataIndicators.size > 0) {
            dataRecyclerView.layoutManager = LinearLayoutManager(context!!)
            dataRecyclerView.adapter = IndicatorsAdapter(indicatorsModel.dataIndicators, context!!)
        } else {
            constInternet.visibility = View.GONE
        }
        if (indicatorsModel.voiceIndicators.size > 0) {
            voiceRecyclerView.layoutManager = LinearLayoutManager(context!!)
            voiceRecyclerView.adapter =
                IndicatorsAdapter(indicatorsModel.voiceIndicators, context!!)
        } else {
            constVoice.visibility = View.GONE
        }
        if (indicatorsModel.smsIndicators.size > 0) {
            smsRecyclerView.layoutManager = LinearLayoutManager(context!!)
            smsRecyclerView.adapter = IndicatorsAdapter(indicatorsModel.smsIndicators, context!!)
        } else {
            constSms.visibility = View.GONE
        }
    }

//    private fun renderIndicators(
//        indicatorHolder: Map<String, IndicatorHolder>?,
//        chargeDate: String?
//    ) {
//        if (indicatorHolder?.containsKey("DATA")!!) {
//            viewAmountInetConditions.visibility = View.VISIBLE
//            val rest = indicatorHolder["DATA"]?.rest!!
//            val total = indicatorHolder["DATA"]?.total!!
//            tvInetConditionsRest.text =
//                "${StringUtils().unitValueConverter(rest).value} ${StringUtils().unitValueConverter(
//                    rest
//                ).unit}"
//            tvInetConditionsTotal.text =
//                "из ${StringUtils().unitValueConverter(total).value} ${StringUtils().unitValueConverter(
//                    total
//                ).unit}"
//            if (chargeDate == null) {
//                tvInetConditionsDate.visibility = View.GONE
//            } else {
//                tvInetConditionsDate.text =
//                    "до ${TimeUtils().changeFormat(chargeDate, "yyyy-MM-dd", "dd.MM.yyyy")}"
//            }
//            pbInetConditions.progress = indicatorHolder["DATA"]?.percent!!
//        }
//        if (indicatorHolder?.containsKey("VOICE")!!) {
//            constVoice.visibility = View.VISIBLE
//            val rest = indicatorHolder["VOICE"]?.rest!!
//            val total = indicatorHolder["VOICE"]?.total!!
//            tvVoiceConditionsRest.text =
//                "${StringUtils().unitValueConverter(rest).value} ${StringUtils().unitValueConverter(
//                    rest
//                ).unit}"
//            tvConditionsTotal.text =
//                "из ${StringUtils().unitValueConverter(total).value} ${StringUtils().unitValueConverter(
//                    total
//                ).unit}"
//            if (chargeDate == null) {
//                tvConditionsDate.visibility = View.GONE
//            } else {
//                tvConditionsDate.text =
//                    "до ${TimeUtils().changeFormat(chargeDate, "yyyy-MM-dd", "dd.MM.yyyy")}"
//            }
//            pbConditions.progress = indicatorHolder["VOICE"]?.percent!!
//        }
//        if (indicatorHolder?.containsKey("SMS")!!) {
//            constSms.visibility = View.VISIBLE
//            val rest = indicatorHolder["SMS"]?.rest!!
//            val total = indicatorHolder["SMS"]?.total!!
//            tvSmsConditionsRest.text =
//                "${StringUtils().unitValueConverter(rest).value} ${StringUtils().unitValueConverter(
//                    rest
//                ).unit}"
//            tvSmsConditionsTotal.text =
//                "из ${StringUtils().unitValueConverter(total).value} ${StringUtils().unitValueConverter(
//                    total
//                ).unit}"
//            if (chargeDate == null) {
//                tvSmsConditionsDate.visibility = View.GONE
//            } else {
//                tvSmsConditionsDate.text =
//                    "до ${TimeUtils().changeFormat(chargeDate, "yyyy-MM-dd", "dd.MM.yyyy")}"
//            }
//            Log.d("SMS INDICATOR", indicatorHolder["SMS"]?.percent!!.toString())
//            pbSmsConditions.progress = indicatorHolder["SMS"]?.percent!!
//        }
//    }

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
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        activity!!.nav_view.visibility = View.VISIBLE
        var tvTitle: AppCompatTextView = activity!!.findViewById(R.id.tvTitle)
        tvTitle.setTextColor(resources.getColor(R.color.black))
        tvTitle.text = "Мой Тариф"

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_my_tariff, container, false)
    }


}
