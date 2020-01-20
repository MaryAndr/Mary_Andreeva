package kz.atc.mobapp.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxSeekBar
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
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
class MinToGbFragment(val exchangeInfo: ExchangeResponse?) :
    MviFragment<MinToGbView, MinToGbPresenter>(),
    MinToGbView {

    override fun exchangeMinsIntent(): Observable<Int> {
        return RxView.clicks(btnExchange).flatMap {
            val mins = etMin.text.toString().replace("[^0-9]".toRegex(), "")
            Observable.just(mins.toInt())
        }
    }

    private lateinit var preLoadTrigger: BehaviorSubject<Int>

    override fun getExchangeDataIntent(): Observable<Int> {
        return preLoadTrigger
    }

    override fun createPresenter() = MinToGbPresenter(context!!)

    override fun changeQuantityIntent(): Observable<Int> {
        return RxSeekBar.changes(minToGbSeekBar)
    }

    override fun render(state: MinToGbState) {
        when (state) {
            is MinToGbState.EtQuantityChanged -> {
                val gb = state.quantity * exchangeInfo!!.rate
                etMin.setText("${state.quantity} мин")
                etGb.setText(
                    "${(gb / 1024).toBigDecimal().setScale(
                        1,
                        RoundingMode.UP
                    ).toDouble()} ГБ"
                )
            }

            is MinToGbState.Exchanged -> {
                Toast.makeText(context, state.status, Toast.LENGTH_LONG).show()
            }

            is MinToGbState.ExchangeData -> {
                minToGbSeekBar.max = state.data.max_minutes
            }
        }
    }

    override fun onResume() {
        super.onResume()
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
        return inflater.inflate(R.layout.fragment_min_to_gb, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}
