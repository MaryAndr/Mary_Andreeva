package ru.filit.motiv.app.fragments

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.filit.motiv.app.R
import ru.filit.motiv.app.utils.ConnectivityReceiver

class InternetLostFragment: Fragment(), ConnectivityReceiver.ConnectivityReceiverListener {

    private val connectivityReceiver = ConnectivityReceiver()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.no_internet_layout,container,false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity!!.registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            activity?.supportFragmentManager?.popBackStackImmediate()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(connectivityReceiver)
    }
}