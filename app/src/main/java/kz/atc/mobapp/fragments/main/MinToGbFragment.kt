package kz.atc.mobapp.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxSeekBar
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main_page.*
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
    override fun changeIndicatorIntent(): Observable<Int> {
        return RxTextView.textChanges(etMin).map<Int> {
            try {
                it.toString().replace("[^0-9]".toRegex(), "").toInt()
            } catch (ex: Exception) {
                0
            }
        }
    }

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
                if (!etMin.isFocused) {
                    val gb = state.quantity * exchangeInfo!!.rate
                    etMin.setText("${state.quantity} мин")
                    etGb.setText(
                        "${(gb / 1024).toBigDecimal().setScale(
                            1,
                            RoundingMode.UP
                        ).toDouble()} ГБ"
                    )
                }
            }

            is MinToGbState.Exchanged -> {
                Toast.makeText(context, state.status, Toast.LENGTH_LONG).show()
                if (state.status == "Обмен успешно произведен") {
                    val fr = MainPageFragment()
                    val fm = activity!!.supportFragmentManager
                    val fragmentTransaction = fm!!.beginTransaction().addToBackStack("minToGb")
                    fragmentTransaction.replace(R.id.container, fr)
                    fragmentTransaction.commit()
                }
            }

            is MinToGbState.ExchangeData -> {
                minToGbSeekBar.max = state.data.max_minutes
                tvMins.text = "${state.data.max_minutes} Мин"
                tvMbValue.text = "${state.data.rate} Мб"
            }

            is MinToGbState.IndicatorChange -> {
                minToGbSeekBar.progress = state.quantity
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
        tvTitle.text = "Обмен минут на ГБ"
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

        //Для сохранения курсора при изменение значения в editText минут
        minToGbSeekBar.setOnTouchListener { view, motionEvent ->
            etMin.isEnabled = false
            etMin.isEnabled = true
            false
        }
    }

}
