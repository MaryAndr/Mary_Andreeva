package ru.filit.motiv.app.fragments.main


import android.app.AlertDialog
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_change_pass.*

import ru.filit.motiv.app.R
import ru.filit.motiv.app.models.main.ChangePassModel
import ru.filit.motiv.app.presenters.main.ChangePassPresenter
import ru.filit.motiv.app.states.main.ChangePassState
import ru.filit.motiv.app.utils.ConnectivityReceiver
import ru.filit.motiv.app.utils.hideKeyboard
import ru.filit.motiv.app.views.main.ChangePassView

class ChangePassFragment : MviFragment<ChangePassView, ChangePassPresenter>(), ChangePassView
    , ConnectivityReceiver.ConnectivityReceiverListener, TextView.OnEditorActionListener{

    private val connectivityReceiver = ConnectivityReceiver()

    private lateinit var processTrigger: BehaviorSubject<ChangePassModel>

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
                val dialogBuilder = AlertDialog.Builder(this.context)
                dialogBuilder
                    .setMessage(state.result)
                    .setPositiveButton("OK") { _, _ ->
                    }
                    .create()
                    .show()
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
        return processTrigger
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkAvailabilityTrigger = BehaviorSubject.create()
        processTrigger = BehaviorSubject.create()
        activity!!.registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnChangePass.setOnClickListener { processTrigger.onNext(ChangePassModel(etCurrentPass.text.toString(), etNewPass.text.toString())) }
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

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            etNewPass.requestFocus()
            return true
        }

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            processTrigger.onNext(ChangePassModel(etCurrentPass.text.toString(), etNewPass.text.toString()))
            hideKeyboard()
            return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(connectivityReceiver)
    }
}
