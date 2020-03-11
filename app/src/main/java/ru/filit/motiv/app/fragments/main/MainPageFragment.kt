package ru.filit.motiv.app.fragments.main

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.fragment_main_page.*
import kotlinx.android.synthetic.main.fragment_main_page.view.*
import ru.filit.motiv.app.R
import ru.filit.motiv.app.models.main.IndicatorHolder
import ru.filit.motiv.app.presenters.main.MainPagePresenter
import ru.filit.motiv.app.states.main.MainPageState
import ru.filit.motiv.app.utils.ConnectivityReceiver
import ru.filit.motiv.app.utils.StringUtils
import ru.filit.motiv.app.utils.TextConverter
import ru.filit.motiv.app.views.main.MainPageView
import java.math.RoundingMode
import java.text.DecimalFormat


class MainPageFragment : MviFragment<MainPageView, MainPagePresenter>(),
    MainPageView, ConnectivityReceiver.ConnectivityReceiverListener {

    override fun createPresenter() = MainPagePresenter(context!!)

    private val connectivityReceiver = ConnectivityReceiver()

    private lateinit var networkAvailabilityTrigger : BehaviorSubject<Boolean>

    override fun checkInternetConnectivityIntent(): Observable<Boolean> {
        return networkAvailabilityTrigger
    }

    private lateinit var preLoadTrigger: BehaviorSubject<Int>

    override fun preLoadIntent(): Observable<Int> {
        return preLoadTrigger
    }


    override fun render(state: MainPageState) {
        when {
            state.loading -> {
                pgMainData.visibility = View.VISIBLE
                dataView.visibility = View.GONE
                layoutGbToMin.visibility = View.GONE
                layoutServices.visibility = View.GONE
                no_internet_view.visibility = View.GONE
            }
            state.mainDataLoaded -> {
                renderFirstLoad(state)

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
            state.connectionLost -> {
                    dataView.visibility = View.GONE
                    layoutGbToMin.visibility = View.GONE
                    layoutServices.visibility = View.GONE
                    pgMainData.visibility = View.GONE
                    no_internet_view.visibility = View.VISIBLE
            }
            state.connectionResume -> {
                preLoadTrigger.onNext(0)
            }
        }
    }

    private fun renderFirstLoad(state: MainPageState) {
        val phoneNumber = state.mainData?.phoneNumber
        dataView.visibility = View.VISIBLE
        pgMainData.visibility = View.GONE
        no_internet_view.visibility = View.GONE
        layoutServices.visibility = View.VISIBLE

        layoutServices.setOnClickListener {
            val fr = ServicesFragment()
            val fm = activity!!.supportFragmentManager
            val fragmentTransaction = fm!!.beginTransaction().addToBackStack("main_page")
            fragmentTransaction.replace(R.id.container, fr)
            fragmentTransaction.commit()
        }
        if (state.mainData?.subExchange?.available!!) {
            layoutGbToMin.visibility = View.VISIBLE
            layoutGbToMin.setOnClickListener {
                val fr = MinToGbFragment(state.mainData?.subExchange)
                val fm = activity!!.supportFragmentManager
                val fragmentTransaction = fm!!.beginTransaction()
                fragmentTransaction.addToBackStack("min_to_gb")
                fragmentTransaction.replace(R.id.container, fr)
                fragmentTransaction.commit()
            }
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

                if (rest != 0 || total != 0) {
                    visibilityDataGroup(View.VISIBLE)
                    val df = DecimalFormat("#.##")
                    df.roundingMode = RoundingMode.HALF_UP
                    pbInternet.progress = indicatorHolder["DATA"]?.percent!!
                    tvDataRestAmount.text =
                        "${df.format(StringUtils().unitValueConverter(rest).value)} ${StringUtils().unitValueConverter(
                            rest
                        ).unit}"
                    tvDataTotalAmount.text =
                        "из ${df.format(StringUtils().unitValueConverter(total).value)} ${StringUtils().unitValueConverter(
                            total
                        ).unit}"
                }
            } else if (indicatorHolder["DATA"]!!.unlim) {
                visibilityDataGroup(View.VISIBLE)
                pbInternet.visibility = View.INVISIBLE
                tvDataRestAmount.text = "Безлимит"
                tvDataTotalAmount.text = indicatorHolder["DATA"]?.optionsName
                if (indicatorHolder["DATA"]?.optionsName!!.length > 25) {
                    tvDataTotalAmount.textSize = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        11f,
                        resources.displayMetrics
                    )
                }
            } else if (indicatorHolder["DATA"]!!.valueUnit != null) {
                visibilityDataGroup(View.VISIBLE)
                pbInternet.visibility = View.INVISIBLE
                tvDataRestAmount.text = indicatorHolder["DATA"]!!.valueUnit
                tvDataTotalAmount.text = "Интернет"
            }
        }
        if (indicatorHolder.containsKey("VOICE")) {
            Log.d("HERE", "VOICES")
            if (!indicatorHolder["VOICE"]!!.unlim && indicatorHolder["VOICE"]!!.valueUnit == null) {
                var rest = indicatorHolder["VOICE"]?.rest!!
                var total = indicatorHolder["VOICE"]?.total!!

                if (rest != 0 || total != 0) {
                    visibilityVoiceGroup(View.VISIBLE)
                    pbPhone.progress = indicatorHolder["VOICE"]?.percent!!
                    tvVoiceRestAmount.text = "$rest Мин"
                    tvVoiceTotalAmount.text = "из $total Мин"
                }
            } else if (indicatorHolder["VOICE"]!!.unlim) {
                visibilityVoiceGroup(View.VISIBLE)
                pbPhone.visibility = View.INVISIBLE
                tvVoiceRestAmount.text = "Безлимит"
                tvVoiceTotalAmount.text = indicatorHolder["VOICE"]?.optionsName
                if (indicatorHolder["VOICE"]?.optionsName!!.length > 25) {
                    tvVoiceTotalAmount.textSize = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        11f,
                        resources.displayMetrics
                    )
                }
            } else if (indicatorHolder["VOICE"]!!.valueUnit != null) {
                visibilityVoiceGroup(View.VISIBLE)
                pbPhone.visibility = View.INVISIBLE
                tvVoiceRestAmount.text = indicatorHolder["VOICE"]!!.valueUnit
                tvVoiceTotalAmount.text = "Исходящие звонки"
            }
        }
        if (indicatorHolder.containsKey("SMS")) {
            if (!indicatorHolder["SMS"]!!.unlim && indicatorHolder["SMS"]!!.valueUnit == null) {
                var rest = indicatorHolder["SMS"]?.rest!!
                var total = indicatorHolder["SMS"]?.total!!

                if (rest != 0 || total != 0) {
                    visibilitySMSGroup(View.VISIBLE)
                    pbSms.progress = indicatorHolder["SMS"]?.percent!!
                    tvSMSRestAmount.text = "$rest SMS"
                    tvSmsTotalAmount.text = "из $total SMS"
                }
            } else if (indicatorHolder["SMS"]!!.unlim) {
                visibilitySMSGroup(View.VISIBLE)
                dataView.pbSms.visibility = View.INVISIBLE
                tvSMSRestAmount.text = "Безлимит"
                tvSmsTotalAmount.text = indicatorHolder["SMS"]?.optionsName
                if (indicatorHolder["SMS"]?.optionsName!!.length > 25) {
                    tvSmsTotalAmount.textSize = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        11f,
                        resources.displayMetrics
                    )
                }
            } else if (indicatorHolder["SMS"]!!.valueUnit != null) {
                visibilitySMSGroup(View.VISIBLE)
                dataView.pbSms.visibility = View.INVISIBLE
                tvSMSRestAmount.text = indicatorHolder["SMS"]!!.valueUnit
                tvSmsTotalAmount.text = "SMS"
            }
        }
        /*if (pbInternet.visibility == View.INVISIBLE && pbPhone.visibility == View.INVISIBLE && pbSms.visibility == View.INVISIBLE) {
            dataView.layoutParams.height = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                240f,
                resources.displayMetrics
            ).toInt()
        }*/
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
        networkAvailabilityTrigger = BehaviorSubject.create()
        activity!!.registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }


    override fun onResume() {
        super.onResume()
        activity!!.nav_view.visibility = View.VISIBLE
        (activity as AppCompatActivity).supportActionBar?.apply {
            setCustomView(R.layout.appbar_main_page)
            elevation = 0f
            setDisplayHomeAsUpEnabled(false)
            setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
            val ivProfilePic: AppCompatImageView = activity!!.findViewById(R.id.ivProfilePic)
            ivProfilePic.setOnClickListener {
                val fr = SettingsFragment()
                val fm = activity!!.supportFragmentManager
                val fragmentTransaction = fm!!.beginTransaction().addToBackStack("settings")
                fragmentTransaction.replace(R.id.container, fr)
                fragmentTransaction.commit()

            }
        }
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onStart() {
        super.onStart()
       preLoadTrigger.onNext(0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main_page, container, false)
    }

    override fun onPause() {
        super.onPause()
        pgMainData.visibility = View.GONE
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

        tvTariffName.setOnClickListener {
            activity!!.nav_view.selectedItemId = R.id.navigation_tariff
        }

        visibilityDataGroup(View.INVISIBLE)
        visibilityVoiceGroup(View.INVISIBLE)
        visibilitySMSGroup(View.INVISIBLE)
        layoutGbToMin.visibility = View.GONE
        layoutServices.visibility = View.GONE
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

    private fun visibilitySMSGroup( visibility:Int){
        pbSms.visibility = visibility
        tvSMSRestAmount.visibility = visibility
        tvSmsTotalAmount.visibility = visibility
    }

    private fun visibilityVoiceGroup( visibility:Int){
        pbPhone.visibility = visibility
        tvVoiceRestAmount.visibility = visibility
        tvVoiceTotalAmount.visibility = visibility
    }

    private fun visibilityDataGroup( visibility:Int){
        pbInternet.visibility = visibility
        tvDataRestAmount.visibility = visibility
        tvDataTotalAmount.visibility = visibility
    }
}