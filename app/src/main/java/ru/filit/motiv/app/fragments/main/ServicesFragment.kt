package ru.filit.motiv.app.fragments.main


import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.fragment_services.*

import ru.filit.motiv.app.R
import ru.filit.motiv.app.adapters.EnabledServicesAdapter
import ru.filit.motiv.app.adapters.ExpandableServiceAdapter
import ru.filit.motiv.app.dialogs.ServiceConfirmationDialogMVI
import ru.filit.motiv.app.listeners.OnServiceToggleChangeListner
import ru.filit.motiv.app.models.main.ServiceDialogModel
import ru.filit.motiv.app.models.main.ServicesListShow
import ru.filit.motiv.app.presenters.main.ServicesPresenter
import ru.filit.motiv.app.states.main.ServicesPartialState
import ru.filit.motiv.app.utils.Constants
import ru.filit.motiv.app.utils.TimeUtils
import ru.filit.motiv.app.views.main.ServicesPageView
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ServicesFragment : MviFragment<ServicesPageView, ServicesPresenter>(), ServicesPageView, OnServiceToggleChangeListner {

    override fun createPresenter() = ServicesPresenter(context!!)

    private lateinit var service:ServicesListShow

    private lateinit var allServicesListAdapter:ExpandableServiceAdapter

    private  lateinit var enabledServicesAdapter:EnabledServicesAdapter

    var items = mutableListOf<ServicesListShow>()

    private lateinit var triggerChangeService: BehaviorSubject<String>

    private lateinit var changeRadioGroup: BehaviorSubject<Boolean>

    private lateinit var cancelChange: BehaviorSubject<Boolean>

    override fun showEnabledServiceIntent(): Observable<Boolean> {
        return changeRadioGroup

    }

    override fun render(state: ServicesPartialState) {

        when (state){
              is ServicesPartialState.FetchEnabledService-> {
                  pgData.visibility = View.GONE
                  allServicesLayout.visibility = View.GONE
                  addedServicesLayout.visibility = View.VISIBLE
                  servicesGroup.visibility = View.VISIBLE
                  pgChangeService.visibility = View.GONE
                allButton.isChecked = false
                activeButton.isChecked = true
                  items = state.servicesList
                servicesList.layoutManager = LinearLayoutManager(context!!)
                servicesList.adapter = enabledServicesAdapter
                  enabledServicesAdapter.setData(items)
                (servicesList.adapter as EnabledServicesAdapter).notifyDataSetChanged()
            }
            is ServicesPartialState.FetchAllService -> {
                pgData.visibility = View.GONE
                allServicesLayout.visibility = View.VISIBLE
                servicesGroup.visibility = View.VISIBLE
                addedServicesLayout.visibility = View.GONE
                pgChangeService.visibility = View.GONE
                allButton.isChecked = true
                activeButton.isChecked = false
                items = state.servicesList
                allServicesList.setAdapter(allServicesListAdapter)
                allServicesListAdapter.setData(items)
                servicesList.layoutManager = LinearLayoutManager(context!!)
                servicesList.adapter = enabledServicesAdapter
                enabledServicesAdapter.setData(items)
                (servicesList.adapter as EnabledServicesAdapter).notifyDataSetChanged()
            }
            is ServicesPartialState.Loading -> {
                allServicesLayout.visibility = View.GONE
                addedServicesLayout.visibility = View.GONE
                pgData.visibility = View.VISIBLE
                servicesGroup.visibility = View.VISIBLE
                pgChangeService.visibility = View.GONE
            }

            is ServicesPartialState.LoadingChangeService -> {
                allServicesLayout.visibility = View.GONE
                addedServicesLayout.visibility = View.GONE
                servicesGroup.visibility = View.GONE
                pgData.visibility = View.GONE
                pgChangeService.visibility = View.VISIBLE
            }

            is ServicesPartialState.ChangeAvailableService -> {
                val dialogBuilder = AlertDialog.Builder(this.context)
                dialogBuilder
                    .setMessage(state.dialogMessage)
                    .setPositiveButton("OK") { _, _ ->
                    }
                    .create()
                    .show()
                changeRadioGroup.onNext(changeRadioGroup.value!!)
            }

            is ServicesPartialState.CancelChange -> {
                items.forEach{if(it.id ==service.id)it.toggleState = service.toggleState }
              if (servicesGroup.checkedRadioButtonId == R.id.activeButton)    {
                  servicesList.layoutManager = LinearLayoutManager(context!!)
                  servicesList.adapter = enabledServicesAdapter
                  enabledServicesAdapter.setData(items)
                  enabledServicesAdapter.setData(items)
                  (servicesList.adapter as EnabledServicesAdapter).notifyDataSetChanged()

              }else {allServicesListAdapter.setData(items)}
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
        triggerChangeService = BehaviorSubject.create()
        changeRadioGroup = BehaviorSubject.create()
        cancelChange = BehaviorSubject.create()
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
        allServicesListAdapter = ExpandableServiceAdapter(context!!, this)
        enabledServicesAdapter =EnabledServicesAdapter(context!!, this)
        changeRadioGroup.onNext(true)
        servicesGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId==R.id.activeButton) changeRadioGroup.onNext(true)
            else changeRadioGroup.onNext(false)
        }
    }

    override fun onToggleClick(item: ServicesListShow, isChecked:Boolean) {
            service = item
            val dataToPass = ServiceDialogModel()
            dataToPass.serv_name = service.serviceName
            dataToPass.serv_id = service.id
            dataToPass.isConnection = isChecked
            dataToPass.activationPrice = service.activPrice
            dataToPass.abonPay = service.subFee
            dataToPass.conDate = TimeUtils().dateToString(Calendar.getInstance())
            val dialog = ServiceConfirmationDialogMVI.newInstance(dataToPass)
            dialog.setTargetFragment(this,Constants.REQUEST_CODE_SERVICE)
            dialog.show(
                (context as AppCompatActivity).supportFragmentManager,
                "Accept Dialog"
            )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_CODE_SERVICE&&resultCode == Activity.RESULT_OK){
            triggerChangeService.onNext(data!!.extras.getString(Constants.SERVICE_DIALOG_MESSAGE))
        }else{
            cancelChange.onNext(true)
        }
    }

    override fun changeServiceIntent(): Observable<String> {
        return triggerChangeService
    }

    override fun cancelChangeServiceIntent(): Observable<Boolean> {
        return cancelChange
    }
}
