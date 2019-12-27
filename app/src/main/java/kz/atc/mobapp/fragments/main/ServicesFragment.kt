package kz.atc.mobapp.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_services.*

import kz.atc.mobapp.R
import kz.atc.mobapp.adapters.EnabledServicesAdapter
import kz.atc.mobapp.presenters.main.ServicesPresenter
import kz.atc.mobapp.states.main.ServicesState
import kz.atc.mobapp.views.main.ServicesPageView

/**
 * A simple [Fragment] subclass.
 */
class ServicesFragment : MviFragment<ServicesPageView, ServicesPresenter>(), ServicesPageView {

    override fun createPresenter() = ServicesPresenter(context!!)

    private lateinit var enabledServicesTrigger: BehaviorSubject<Int>

    override fun showEnabledServiceIntent(): Observable<Int> {
        return enabledServicesTrigger
    }

    override fun render(state: ServicesState) {
        when(state) {
            is ServicesState.FetchEnabledService -> {
                servicesList.layoutManager = LinearLayoutManager(context!!)
                servicesList.adapter = EnabledServicesAdapter(context!!, state.servicesList)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enabledServicesTrigger = BehaviorSubject.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_services, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enabledServicesTrigger.onNext(1)
    }


}
