package ru.filit.motiv.app.fragments.main


import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat.getSystemService
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
import ru.filit.motiv.app.dialogs.ServiceConfirmationDialogMVI
import ru.filit.motiv.app.listeners.OnServiceToggleChangeListener
import ru.filit.motiv.app.models.main.IndicatorsModel
import ru.filit.motiv.app.models.main.MyTariffAboutData
import ru.filit.motiv.app.models.main.ServiceDialogModel
import ru.filit.motiv.app.models.main.ServicesListShow
import ru.filit.motiv.app.presenters.main.MyTariffPresenter
import ru.filit.motiv.app.states.main.MyTariffState
import ru.filit.motiv.app.utils.ConnectivityReceiver
import ru.filit.motiv.app.utils.Constants
import ru.filit.motiv.app.utils.TextConverter
import ru.filit.motiv.app.utils.TimeUtils
import ru.filit.motiv.app.views.main.MyTariffView
import java.util.*

class MyTariffFragment : MviFragment<MyTariffView, MyTariffPresenter>(),
    MyTariffView, OnServiceToggleChangeListener, ConnectivityReceiver.ConnectivityReceiverListener {

    override fun createPresenter() = MyTariffPresenter(context!!)

    private lateinit var preLoadTrigger: BehaviorSubject<Int>
    private lateinit var triggerChangeService: BehaviorSubject<String>
    private lateinit var cancelChange: BehaviorSubject<String>
    private lateinit var networkAvailabilityTrigger : BehaviorSubject<Boolean>
    private val connectivityReceiver = ConnectivityReceiver()

    override fun checkInternetConnectivityIntent(): Observable<Boolean> {
        return networkAvailabilityTrigger
    }

    override fun preLoadIntent(): Observable<Int> {
        return preLoadTrigger
    }

    override fun changeServiceIntent(): Observable<String> {
        return triggerChangeService
    }

    override fun cancelChangeServiceIntent(): Observable<String> {
        return cancelChange
    }

    override fun render(state: MyTariffState) {
        when {
            state.loading -> {
                Log.d("loading", "triggered")
                mainConstraint.visibility = View.GONE
                pgMainData.visibility = View.VISIBLE
                no_internet_view.visibility = View.GONE
            }
            state.mainDataLoaded -> {
                no_internet_view.visibility = View.GONE
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
                        }
                    }
                    .create()
                    .show()
            }
            state.changeService -> {
                val dialogBuilder = AlertDialog.Builder(this.context)
                dialogBuilder
                    .setMessage(state.changeServiceMessage)
                    .setPositiveButton("OK") { _, _ ->
                    }
                    .create()
                    .show()
                preLoadTrigger.onNext(1)
            }
            state.connectionLost -> {
                mainConstraint.visibility = View.GONE
                pgMainData.visibility = View.GONE
                no_internet_view.visibility = View.VISIBLE
            }
            state.connectionResume -> {
                preLoadTrigger.onNext(1)
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
                subTariff?.tariff?.constructor?.min,
                subTariff?.tariff?.constructor?.data,
                subTariff?.tariff?.constructor?.sms
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
                    textSize = 14f
                    setTextColor(resources.getColor(R.color.grey))
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
            MyTariffServicesAdapter(state.mainData?.servicesList, context!!, this)

        renderNewIndicators(state.mainData?.indicatorModels!!)
//                renderIndicators(state.mainData?.indicatorHolder, subTariff?.charge_date)
    }

    private fun renderNewIndicators(indicatorsModel: IndicatorsModel) {
        val indicatorSMS = indicatorsModel.smsIndicators
        val indicatorVoice =indicatorsModel.voiceIndicators
        val indicatorData = indicatorsModel.dataIndicators

        if (indicatorData.size > 0) {
            dataRecyclerView.layoutManager = LinearLayoutManager(context!!)
            dataRecyclerView.adapter = IndicatorsAdapter(
                indicatorData
                , context!!
            )
        } else {
            constInternet.visibility = View.GONE
        }
        if (indicatorVoice.size > 0) {
            voiceRecyclerView.layoutManager = LinearLayoutManager(context!!)
            voiceRecyclerView.adapter =
                IndicatorsAdapter(
                    indicatorVoice
                    , context!!)
        } else {
            constVoice.visibility = View.GONE
        }
        if (indicatorSMS.size > 0) {
            smsRecyclerView.layoutManager = LinearLayoutManager(context!!)
            smsRecyclerView.adapter = IndicatorsAdapter(
                indicatorSMS
                , context!!)
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
        preLoadTrigger = BehaviorSubject.create()
        triggerChangeService = BehaviorSubject.create()
        cancelChange = BehaviorSubject.create()
        networkAvailabilityTrigger = BehaviorSubject.create()
        activity!!.registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        activity!!.nav_view.visibility = View.VISIBLE
        val tvTitle: AppCompatTextView = activity!!.findViewById(R.id.tvTitle)
        tvTitle.setTextColor(resources.getColor(R.color.black))
        tvTitle.text = "Мой Тариф"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_my_tariff, container, false)
    }

    override fun onStart() {
        super.onStart()
        preLoadTrigger.onNext(0)
    }

    override fun onToggleClick(item: ServicesListShow, isChecked: Boolean, position: Int) {
        if (!isChecked) {
            val dataToPass = ServiceDialogModel()
            dataToPass.serv_name = item.serviceName
            dataToPass.serv_id = item.id
            dataToPass.isConnection = isChecked
            dataToPass.activationPrice = item.activPrice
            dataToPass.abonPay = item.subFee
            dataToPass.conDate = TimeUtils().dateToString(Calendar.getInstance())
            val dialog = ServiceConfirmationDialogMVI.newInstance(dataToPass)
            dialog.setTargetFragment(this, Constants.REQUEST_CODE_SERVICE)
            dialog.show(
                (context as AppCompatActivity).supportFragmentManager,
                "Accept Dialog"
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_CODE_SERVICE&&resultCode == Activity.RESULT_OK){
            triggerChangeService.onNext(data!!.extras.getString(Constants.SERVICE_DIALOG_MESSAGE))
        }else{
            cancelChange.onNext(data!!.extras.getString(Constants.SERVICE_ID))
        }
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
