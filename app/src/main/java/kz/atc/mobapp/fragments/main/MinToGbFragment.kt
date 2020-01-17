package kz.atc.mobapp.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.widget.RxSeekBar
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_min_to_gb.*

import kz.atc.mobapp.R
import kz.atc.mobapp.presenters.main.MinToGbPresenter
import kz.atc.mobapp.states.main.MinToGbState
import kz.atc.mobapp.views.main.MinToGbView

/**
 * A simple [Fragment] subclass.
 */
class MinToGbFragment : MviFragment<MinToGbView, MinToGbPresenter>(),
    MinToGbView {

    override fun createPresenter() = MinToGbPresenter(context!!)

    override fun changeQuantityIntent(): Observable<Int> {
        return RxSeekBar.changes(minToGbSeekBar)
    }

    override fun render(state: MinToGbState) {
        when (state) {
            is MinToGbState.EtQuantityChanged -> {
                etMin.setText("${state.quantity} мин")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_min_to_gb, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        minToGbSeekBar.max = 1550
    }

}
