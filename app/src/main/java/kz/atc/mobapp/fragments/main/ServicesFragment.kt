package kz.atc.mobapp.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import com.jakewharton.rxbinding2.widget.RxRadioGroup
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_services.*

import kz.atc.mobapp.R
import kz.atc.mobapp.adapters.EnabledServicesAdapter
import kz.atc.mobapp.adapters.ExpandableServiceAdapter
import kz.atc.mobapp.models.main.ServicesListShow
import kz.atc.mobapp.presenters.main.ServicesPresenter
import kz.atc.mobapp.states.main.ServicesState
import kz.atc.mobapp.views.main.ServicesPageView

/**
 * A simple [Fragment] subclass.
 */
class ServicesFragment : MviFragment<ServicesPageView, ServicesPresenter>(), ServicesPageView {

    override fun createPresenter() = ServicesPresenter(context!!)

    override fun showEnabledServiceIntent(): Observable<Boolean> {
        return RxRadioGroup.checkedChanges(servicesGroup)
            .flatMap {
                if(it == R.id.activeButton) {
                    Observable.just(true)
                } else {
                    Observable.just(false)
                }
            }

    }

    override fun render(state: ServicesState) {
        when(state) {
            is ServicesState.FetchEnabledService -> {
                allServicesLayout.visibility = View.GONE
                addedServicesLayout.visibility = View.VISIBLE
                allButton.isChecked = false
                activeButton.isChecked = true
                servicesList.layoutManager = LinearLayoutManager(context!!)
                servicesList.adapter = EnabledServicesAdapter(context!!, state.servicesList)
                (servicesList.adapter as EnabledServicesAdapter).notifyDataSetChanged()
            }
            is ServicesState.FetchAllService -> {
                allServicesLayout.visibility = View.VISIBLE
                addedServicesLayout.visibility = View.GONE
                allButton.isChecked = true
                activeButton.isChecked = false
                val titles = state.servicesList.distinctBy { it.category }.map { it.category }
                val mapOfServices = mutableMapOf<String, MutableList<ServicesListShow>>()
                titles.forEach{title ->
                    mapOfServices[title!!] = state.servicesList.filter { it.category == title }.toMutableList()
                }
                val adapter = ExpandableServiceAdapter(context!!,titles,mapOfServices)
                allServicesList.setAdapter(adapter)
                servicesList.layoutManager = LinearLayoutManager(context!!)
                servicesList.adapter = EnabledServicesAdapter(context!!, state.servicesList)
                (servicesList.adapter as EnabledServicesAdapter).notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    }


}
