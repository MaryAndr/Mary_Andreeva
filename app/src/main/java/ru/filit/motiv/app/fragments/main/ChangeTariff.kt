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
            is ChangeTariffState.Loading -> {
                pb.visibility = View.VISIBLE
                mainDataView.visibility = View.GONE
            }
            is ChangeTariffState.MainDataLoaded -> {
                pb.visibility = View.GONE
                mainDataView.visibility = View.VISIBLE
                renderCurrentTariff(state.data.first{pred -> pred.isCurrent})
                val tariffList = state.data.filter { pred -> !pred.isCurrent }
                val titles = tariffList.distinctBy { it.category }.map { it.category }
                val mapOfTariffs = mutableMapOf<String, MutableList<TariffShow>>()
                titles.forEach { title ->
                    mapOfTariffs[title!!] =
                        tariffList.filter { it.category == title }.toMutableList()
                }

                val adapter = ExpandableTariffsCategories(context!!, titles, mapOfTariffs, preLoadTrigger)
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
            val text = child.dataValueUnit
            addDataView.visibility = View.VISIBLE
            tvAddData.text = text
        } else {
            addDataView.visibility = View.INVISIBLE
        }

        if (child.voiceValueUnit != null) {
            addVoiceView.visibility = View.VISIBLE
            tvAddVoice.text = child.voiceValueUnit
        } else {
            addVoiceView.visibility = View.INVISIBLE
        }

        if (child.smsValueUnit != null) {
            addSMSView.visibility = View.VISIBLE
            tvAddSMS.text = child.smsValueUnit
        } else {
            addSMSView.visibility = View.INVISIBLE
        }

        if (child.price != null) {
            tvPrice.text =
                "${child.price} ${resources.getString(R.string.rub_value)}/месяц"
        } else {
            tvPrice.visibility = View.INVISIBLE
        }

        if (child.description != null) {
            tvDescription.text = child.description
        } else {
            tvDescription.visibility = View.INVISIBLE
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
        preLoadTrigger.onNext(1)
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

    }


}
