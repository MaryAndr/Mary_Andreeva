package kz.atc.mobapp.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.fragment_settings.*

import kz.atc.mobapp.R
import kz.atc.mobapp.presenters.main.SettingsPresenter
import kz.atc.mobapp.states.main.SettingsState
import kz.atc.mobapp.views.main.SettingsView

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : MviFragment<SettingsView, SettingsPresenter>(), SettingsView {

    private lateinit var preLoadTrigger: BehaviorSubject<Int>

    override fun createPresenter() = SettingsPresenter(context!!)

    override fun mainDataLoadingIntent(): Observable<Int> {
        return preLoadTrigger
    }

    override fun render(state: SettingsState) {
        when(state) {
            is SettingsState.Loading -> {
                mainDataHolder.visibility = View.GONE
                pgMainData.visibility = View.VISIBLE
            }
            is SettingsState.MainDataLoaded -> {
                pgMainData.visibility = View.GONE
                mainDataHolder.visibility = View.VISIBLE

                tvName.text = state.data.full_name
                tvPhoneValue.text = state.data.msisdn
                tvLcNumberValue.text = state.data.pers_account
                tvContractValue.text = state.data.contractInfo
                tvRegionValue.text = state.data.region

                if (state.data.statusId == 1) {
                    tvStatus.text = "Активен"
                    ivLight.setImageResource(R.drawable.active_circle)
                } else if (state.data.statusId == 4) {
                    tvStatus.text = "Приостановлен"
                    ivLight.setImageResource(R.drawable.inactive_circle)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preLoadTrigger = BehaviorSubject.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_backbutton_black)
        var tvTitle: AppCompatTextView = activity!!.findViewById(R.id.tvTitle)
        activity!!.nav_view.visibility = View.INVISIBLE
        tvTitle.setTextColor(resources.getColor(R.color.black))
        tvTitle.text = "Настройки абонента"
        preLoadTrigger.onNext(1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}
