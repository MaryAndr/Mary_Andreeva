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
import kz.atc.mobapp.models.ExchangeResponse
import kz.atc.mobapp.presenters.main.MinToGbPresenter
import kz.atc.mobapp.states.main.MinToGbState
import kz.atc.mobapp.views.main.MinToGbView
import java.math.RoundingMode

/**
 * A simple [Fragment] subclass.
 */
class MinToGbFragment(val exchangeInfo : ExchangeResponse?) : MviFragment<MinToGbView, MinToGbPresenter>(),
    MinToGbView {

    override fun createPresenter() = MinToGbPresenter(context!!)

    override fun changeQuantityIntent(): Observable<Int> {
        return RxSeekBar.changes(minToGbSeekBar)
    }

    override fun render(state: MinToGbState) {
        when (state) {
            is MinToGbState.EtQuantityChanged -> {
                val gb = state.quantity * exchangeInfo!!.rate
                etMin.setText("${state.quantity} мин")
                etGb.setText("${(gb/1024).toBigDecimal().setScale(1, RoundingMode.UP).toDouble()} ГБ")
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

        minToGbSeekBar.max = exchangeInfo!!.max_minutes
    }

}
