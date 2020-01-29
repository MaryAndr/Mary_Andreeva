package ru.filit.motiv.app.fragments.main


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
import kotlinx.android.synthetic.main.info_item.*

import ru.filit.motiv.app.R
import ru.filit.motiv.app.adapters.ExpandableTariffsCategories
import ru.filit.motiv.app.dialogs.MyTariffAboutDialog
import ru.filit.motiv.app.models.main.TariffShow
import ru.filit.motiv.app.presenters.main.ChangeTariffPresenter
import ru.filit.motiv.app.states.main.ChangeTariffState
import ru.filit.motiv.app.views.main.ChangeTariffView

/**
 * A simple [Fragment] subclass.
 */
class ChangeTariff : MviFragment<ChangeTariffView, ChangeTariffPresenter>(), ChangeTariffView {

    override fun createPresenter() = ChangeTariffPresenter(context!!)

    private lateinit var preLoadTrigger: BehaviorSubject<Int>

    override fun showMainDataIntent(): Observable<Int> {
        return preLoadTrigger
    }

    override fun render(state: ChangeTariffState) {
        when (state) {
            is ChangeTariffState.MainDataLoaded -> {
                currentTariffView.visibility = View.VISIBLE
                renderCurrentTariff(state.data.first{pred -> pred.isCurrent})
                state.data.removeAll { pred -> pred.isCurrent }
                val titles = state.data.distinctBy { it.category }.map { it.category }
                val mapOfTariffs = mutableMapOf<String, MutableList<TariffShow>>()
                titles.forEach { title ->
                    mapOfTariffs[title!!] =
                        state.data.filter { it.category == title }.toMutableList()
                }

                val adapter = ExpandableTariffsCategories(context!!, titles, mapOfTariffs)
                tariffs_list.setAdapter(adapter)
            }
        }
    }

    private fun renderCurrentTariff(child: TariffShow) {
        if (child.isNew) {
            ivNew.visibility = View.VISIBLE
        }

        tvName.text = child.name

        if (child.dataValueUnit != null) {
            addDataView.visibility = View.VISIBLE
            tvAddData.text = child.dataValueUnit
        } else {
            addDataView.visibility = View.GONE
        }

        if (child.voiceValueUnit != null) {
            addVoiceView.visibility = View.VISIBLE
            tvAddVoice.text = child.voiceValueUnit
        } else {
            addVoiceView.visibility = View.GONE
        }

        if (child.smsValueUnit != null) {
            addSMSView.visibility = View.VISIBLE
            tvAddSMS.text = child.smsValueUnit
        } else {
            addSMSView.visibility = View.GONE
        }

        if (child.price != null) {
            tvPrice.text =
                "${child.price} ${resources.getString(R.string.rub_value)}/месяц"
        } else {
            tvPrice.visibility = View.GONE
        }

        if (child.description != null) {
            tvDescription.text = child.description
        } else {
            tvDescription.visibility = View.GONE
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
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_backbutton_black)
        activity!!.nav_view.visibility = View.VISIBLE
        var tvTitle: AppCompatTextView = activity!!.findViewById(R.id.tvTitle)
        tvTitle.setTextColor(resources.getColor(R.color.black))
        tvTitle.text = "Тарифы"
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
        return inflater.inflate(R.layout.fragment_change_tariff, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preLoadTrigger.onNext(1)
    }


}
