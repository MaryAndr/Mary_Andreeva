package ru.filit.motiv.app.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.widget.RxRadioGroup
import io.reactivex.Observable
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.fragment_services.*

import ru.filit.motiv.app.R
import ru.filit.motiv.app.adapters.EnabledServicesAdapter
import ru.filit.motiv.app.adapters.ExpandableServiceAdapter
import ru.filit.motiv.app.models.main.ServicesListShow
import ru.filit.motiv.app.presenters.main.ServicesPresenter
import ru.filit.motiv.app.states.main.ServicesState
import ru.filit.motiv.app.views.main.ServicesPageView

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
                pgData.visibility = View.GONE
                allServicesLayout.visibility = View.GONE
                addedServicesLayout.visibility = View.VISIBLE
                allButton.isChecked = false
                activeButton.isChecked = true
                servicesList.layoutManager = LinearLayoutManager(context!!)
                servicesList.adapter = EnabledServicesAdapter(context!!, state.servicesList)
                (servicesList.adapter as EnabledServicesAdapter).notifyDataSetChanged()
            }
            is ServicesState.FetchAllService -> {
                pgData.visibility = View.GONE
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
            is ServicesState.Loading -> {
                allServicesLayout.visibility = View.GONE
                addedServicesLayout.visibility = View.GONE
                pgData.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_backbutton_black)
        var tvTitle: AppCompatTextView = activity!!.findViewById(R.id.tvTitle)
        activity!!.nav_view.visibility = View.INVISIBLE
        tvTitle.setTextColor(resources.getColor(R.color.black))
        tvTitle.text = "Услуги"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RxJavaPlugins.setErrorHandler { throwable ->
            throwable.printStackTrace()
        }

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
