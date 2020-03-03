package ru.filit.motiv.app.fragments.main


import android.app.AlertDialog
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.fragment_change_tariff.*

import ru.filit.motiv.app.R
import ru.filit.motiv.app.adapters.ExpandableTariffsCategories
import ru.filit.motiv.app.dialogs.MyTariffAboutDialog
import ru.filit.motiv.app.models.main.TariffShow
import ru.filit.motiv.app.presenters.main.ChangeTariffPresenter
import ru.filit.motiv.app.states.main.ChangeTariffState
import ru.filit.motiv.app.utils.ConnectivityReceiver
import ru.filit.motiv.app.views.main.ChangeTariffView

/**
 * A simple [Fragment] subclass.
 */
class ChangeTariff : MviFragment<ChangeTariffView, ChangeTariffPresenter>(), ChangeTariffView, ConnectivityReceiver.ConnectivityReceiverListener{

    private val connectivityReceiver = ConnectivityReceiver()
    private lateinit var preLoadTrigger: BehaviorSubject<Int>
    private lateinit var networkAvailabilityTrigger : BehaviorSubject<Boolean>

    override fun checkInternetConnectivityIntent(): Observable<Boolean> {
        return networkAvailabilityTrigger
    }

    override fun createPresenter() = ChangeTariffPresenter(context!!)



    override fun showMainDataIntent(): Observable<Int> {
        return preLoadTrigger
    }

    override fun render(state: ChangeTariffState) {
        when (state) {
            is ChangeTariffState.Loading -> {
                pb.visibility = View.VISIBLE
                mainDataView.visibility = View.GONE
                no_internet_view.visibility = View.GONE
            }
            is ChangeTariffState.MainDataLoaded -> {
                no_internet_view.visibility = View.GONE
                pb.visibility = View.GONE
                mainDataView.visibility = View.VISIBLE
                renderCurrentTariff(state.data.first{pred -> pred.isCurrent})
                val tariffList = state.data.filter { pred -> !pred.isCurrent }
                val titles = tariffList.distinctBy { it.category }.map { it.category }
                val mapOfTariffs = mutableMapOf<String, MutableList<TariffShow>>()
                titles.forEach { title ->
                    mapOfTariffs[title!!] =
                        tariffList.filter { it.category == title }.toMutableList()
                }

                val adapter = ExpandableTariffsCategories(context!!, titles, mapOfTariffs, preLoadTrigger)
                tariffs_list.setAdapter(adapter)
            }
            is ChangeTariffState.InternetState -> {
                if (state.active){
                    preLoadTrigger.onNext(1)
                }else{
                    pb.visibility = View.GONE
                    mainDataView.visibility = View.GONE
                    no_internet_view.visibility = View.VISIBLE
                }
            }
            is ChangeTariffState.ShowErrorMessage -> {
                val dialogBuilder = AlertDialog.Builder(this.context)
                dialogBuilder
                    .setMessage(state.message)
                    .setPositiveButton("OK") { _, _ ->
                    }
                    .create()
                    .show()
            }
        }
    }

    private fun renderCurrentTariff(child: TariffShow) {
        if (child.isNew) {
            ivNew.visibility = View.VISIBLE
        }

        tvName.text = child.name
        val isSelfTariff = child.aboutData?.subscriberTariff?.tariff?.constructor!=null

        if (child.dataValueUnit != null) {
            var text = child.dataValueUnit
            if (isSelfTariff)text+=" Гб"
            addDataView.visibility = View.VISIBLE
            tvAddData.text = text
        } else {
            addDataView.visibility = View.INVISIBLE
        }

        if (child.voiceValueUnit != null) {
            addVoiceView.visibility = View.VISIBLE
            var text = child.voiceValueUnit
            if (isSelfTariff)text+=" Мин"
            tvAddVoice.text = text
        } else {
            addVoiceView.visibility = View.INVISIBLE
        }

        if (child.smsValueUnit != null) {
            addSMSView.visibility = View.VISIBLE
            var text = child.smsValueUnit
            if (isSelfTariff)text+=" SMS"
            tvAddSMS.text = text
        } else {
            addSMSView.visibility = View.INVISIBLE
        }

        if (child.price != null) {
            tvPrice.text =
                "${child.price} ${resources.getString(R.string.rub_value)}/${child.interval}"
        } else {
            tvPrice.visibility = View.INVISIBLE
        }

        if (child.description != null) {
            tvDescription.text = child.description
        } else {
            tvDescription.visibility = View.INVISIBLE
        }

        if (child.aboutData != null) {
            val activity = context as FragmentActivity
            tvDetails.setOnClickListener {
                val aboutDialog = MyTariffAboutDialog.newInstance(child.aboutData!!, false)
                aboutDialog.show(
                    activity!!.supportFragmentManager,
                    "my_tariff_dialog_fragment"
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_backbutton_black)
        activity!!.nav_view.visibility = View.VISIBLE
        var tvTitle: AppCompatTextView = activity!!.findViewById(R.id.tvTitle)
        tvTitle.setTextColor(resources.getColor(R.color.black))
        tvTitle.text = "Тарифы"
    }

    override fun onStart() {
        super.onStart()
        preLoadTrigger.onNext(1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preLoadTrigger = BehaviorSubject.create()
        networkAvailabilityTrigger = BehaviorSubject.create()
        activity!!.registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_tariff, container, false)
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
