package ru.filit.motiv.app.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_change_tariff.*

import ru.filit.motiv.app.R
import ru.filit.motiv.app.adapters.ExpandableTariffsCategories
import ru.filit.motiv.app.models.main.TariffShow
import ru.filit.motiv.app.presenters.main.ChangeTariffPresenter
import ru.filit.motiv.app.states.main.ChangeTariffState
import ru.filit.motiv.app.views.main.ChangeTariffView

/**
 * A simple [Fragment] subclass.
 */
class ChangeTariff : MviFragment<ChangeTariffView, ChangeTariffPresenter>(), ChangeTariffView  {

    override fun createPresenter() = ChangeTariffPresenter(context!!)

    private lateinit var preLoadTrigger: BehaviorSubject<Int>

    override fun showMainDataIntent(): Observable<Int> {
        return preLoadTrigger
    }

    override fun render(state: ChangeTariffState) {
        when (state) {
            is ChangeTariffState.MainDataLoaded -> {
                val titles = state.data.distinctBy { it.category }.map { it.category }
                val mapOfTariffs = mutableMapOf<String, MutableList<TariffShow>>()
                titles.forEach{title ->
                    mapOfTariffs[title!!] = state.data.filter { it.category == title }.toMutableList()
                }

                val adapter = ExpandableTariffsCategories(context!!,titles,mapOfTariffs)
                tariffs_list.setAdapter(adapter)
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
        return inflater.inflate(R.layout.fragment_change_tariff, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preLoadTrigger.onNext(1)
    }


}
