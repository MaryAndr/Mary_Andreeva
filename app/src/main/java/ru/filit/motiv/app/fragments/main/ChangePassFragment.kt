package ru.filit.motiv.app.fragments.main


import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_change_pass.*

import ru.filit.motiv.app.R
import ru.filit.motiv.app.models.main.ChangePassModel
import ru.filit.motiv.app.presenters.main.ChangePassPresenter
import ru.filit.motiv.app.states.main.ChangePassState
import ru.filit.motiv.app.utils.ConnectivityReceiver
import ru.filit.motiv.app.views.main.ChangePassView

class ChangePassFragment : MviFragment<ChangePassView, ChangePassPresenter>(), ChangePassView
    , ConnectivityReceiver.ConnectivityReceiverListener{

    private val connectivityReceiver = ConnectivityReceiver()

    private lateinit var networkAvailabilityTrigger : BehaviorSubject<Boolean>

    override fun checkInternetConnectivityIntent(): Observable<Boolean> {
        return networkAvailabilityTrigger
    }

    override fun render(state: ChangePassState) {
        when (state) {
            is ChangePassState.Processed -> {
                no_internet_view.visibility = View.GONE
                btnChangePass.isEnabled = true
                etCurrentPass.isEnabled = true
                etNewPass.isEnabled = true
                Toast.makeText(context, state.result, Toast.LENGTH_LONG).show()
            }
            is ChangePassState.Loading -> {
                btnChangePass.isEnabled = false
                etCurrentPass.isEnabled = false
                etNewPass.isEnabled = false
            }
            is ChangePassState.ValidationError -> {
                if (state.errCurrPass != null) {
                    layoutTextInputCurrentPass.error = state.errCurrPass
                }
                if (state.errNewPass != null) {
                    layoutTextInputNewPass.error = state.errNewPass
                }
            }

            is ChangePassState.InternetState -> {
                if (state.active){
                    no_internet_view.visibility = View.GONE
                    btnChangePass.visibility = View.VISIBLE
                }else {
                    no_internet_view.visibility = View.VISIBLE
                    btnChangePass.visibility = View.GONE

                }
            }
        }
    }

    override fun createPresenter() = ChangePassPresenter(context!!)

    override fun processIntent(): Observable<ChangePassModel> {
        return RxView.clicks(btnChangePass)
            .map<ChangePassModel> {
                Log.d("debug","test")
                ChangePassModel(etCurrentPass.text.toString(), etNewPass.text.toString())
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkAvailabilityTrigger = BehaviorSubject.create()
        activity!!.registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_pass, container, false)
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
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
