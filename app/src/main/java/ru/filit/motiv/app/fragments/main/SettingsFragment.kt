package ru.filit.motiv.app.fragments.main


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.filit.motiv.app.MainActivity

import ru.filit.motiv.app.R
import ru.filit.motiv.app.presenters.main.SettingsPresenter
import ru.filit.motiv.app.states.main.SettingsState
import ru.filit.motiv.app.utils.Constants
import ru.filit.motiv.app.utils.PreferenceHelper
import ru.filit.motiv.app.utils.PreferenceHelper.set
import ru.filit.motiv.app.utils.TextConverter
import ru.filit.motiv.app.views.main.SettingsView

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment() : MviFragment<SettingsView, SettingsPresenter>(), SettingsView {

    override fun logoutIntent(): Observable<Any> {
        return RxView.clicks(viewExit)
    }

    private lateinit var preLoadTrigger: BehaviorSubject<Int>

    override fun createPresenter() = SettingsPresenter(context!!)

    override fun mainDataLoadingIntent(): Observable<Int> {
        return preLoadTrigger
    }

    override fun render(state: SettingsState) {
        when(state) {
            is SettingsState.LogOut -> {
                val prefs = PreferenceHelper.customPrefs(context!!, Constants.AUTH_PREF_NAME)
                prefs[Constants.AUTH_TOKEN] = null
                prefs[Constants.AUTH_REFRESH_TOKEN] = null
                val intent = Intent(activity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                activity!!.startActivity(intent)
            }
            is SettingsState.Loading -> {
                mainDataHolder.visibility = View.GONE
                pgMainData.visibility = View.VISIBLE
            }
            is SettingsState.MainDataLoaded -> {
                pgMainData.visibility = View.GONE
                mainDataHolder.visibility = View.VISIBLE
                tvName.text = state.data.full_name
                val textConverter = TextConverter()
                tvPhoneValue.text =textConverter.getFormattedPhone(state.data.msisdn!!)
                tvLcNumberValue.text = state.data.pers_account
                tvContractValue.text = state.data.contractInfo
                tvRegionValue.text = state.data.region

                if (state.data.statusId == 1) {
                    tvStatus.text = "Активен"
                    ivLight.setImageResource(R.drawable.active_circle)
                    tvBlockUnblock.text = "Блокировать номер"
                } else if (state.data.statusId == 4) {
                    tvStatus.text = "Приостановлен"
                    ivLight.setImageResource(R.drawable.inactive_circle)
                    tvBlockUnblock.text = "Разблокировать номер"
                }

                viewBlockUnblockPass.setOnClickListener {
                    val fr = BlockUnblockFragment(state.data)
                    val fm = activity!!.supportFragmentManager
                    val fragmentTransaction = fm!!.beginTransaction().addToBackStack("Settings_tag")
                    fragmentTransaction.replace(R.id.container, fr)
                    fragmentTransaction.commit()
                }

                viewChangePass.setOnClickListener{
                    val fr = ChangePassFragment()
                    val fm = activity!!.supportFragmentManager
                    val fragmentTransaction = fm.beginTransaction().addToBackStack("Settings_tag")
                    fragmentTransaction.replace(R.id.container, fr)
                    fragmentTransaction.commit()
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
}
