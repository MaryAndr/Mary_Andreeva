package ru.filit.motiv.app.fragments.main


import android.app.AlertDialog
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

import ru.filit.motiv.app.R
import ru.filit.motiv.app.models.ExchangeResponse
import ru.filit.motiv.app.presenters.main.MinToGbPresenter
import ru.filit.motiv.app.states.main.MinToGbState
import ru.filit.motiv.app.utils.MinMaxFilterValue
import ru.filit.motiv.app.views.main.MinToGbView
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
class MinToGbFragment(private var exchangeInfo: ExchangeResponse?) :
    MviFragment<MinToGbView, MinToGbPresenter>(),
    MinToGbView {
    override fun changeIndicatorIntent(): Observable<Int> {
        return RxTextView.textChanges(etMin).map<Int> {
            try {
                etMin.setSelection(it.length)
                it.toString().replace("[^0-9]".toRegex(), "").toInt()
            } catch (ex: Exception) {
                0
            }
        }
    }

    override fun exchangeMinsIntent(): Observable<Int> {
        return RxView.clicks(btnExchange).flatMap {
            val mins = if (etMin.text.isNullOrEmpty()) { 0 }else {
                etMin.text.toString().replace("[^0-9]".toRegex(), "").toInt()}

                if (0 < mins && mins <= exchangeInfo!!.max_minutes) {
                    Observable.just(mins)
                } else {
                    Observable.just(0)

            }

        }
    }

    private lateinit var preLoadTrigger: BehaviorSubject<Int>

    override fun getExchangeDataIntent(): Observable<Int> {
        return preLoadTrigger
    }

    override fun createPresenter() = MinToGbPresenter(context!!)

    //Без debounce при частом использовании SeekBar'а, приложение фризится
    override fun changeQuantityIntent(): Observable<Int> {
        return RxSeekBar.changes(minToGbSeekBar).debounce(10, TimeUnit.MILLISECONDS)
    }

    override fun render(state: MinToGbState) {
        when (state) {
            is MinToGbState.EtQuantityChanged -> {
                val gb = state.quantity * exchangeInfo!!.rate
                    etMin.setText("${state.quantity}")
                etGb.setText(
                    "${(gb / 1024).toBigDecimal().setScale(
                        2,
                        RoundingMode.UP
                    ).toDouble()} ГБ"
                )
            }

            is MinToGbState.Exchanged -> {
                mainConstaint.visibility = View.VISIBLE
                constraintLayout.visibility = View.VISIBLE
                constraintLayout2.visibility = View.VISIBLE
                pgLoading.visibility = View.GONE
                val dialogBuilder = AlertDialog.Builder(this.context)
                dialogBuilder
                    .setMessage(state.status)
                    .setPositiveButton("OK") { _, _ ->
                    }
                    .create()
                    .show()
                if (state.status == "Обмен Мин на ГБ успешно произведен") {
                    val fr = MainPageFragment()
                    val fm = activity!!.supportFragmentManager
                    val fragmentTransaction = fm.beginTransaction().addToBackStack("minToGb")
                    fragmentTransaction.replace(R.id.container, fr)
                    fragmentTransaction.commit()
                    activity!!.nav_view.selectedItemId = R.id.navigation_home
                }

            }

            is MinToGbState.ExchangeData -> {
                minToGbSeekBar.max = state.data.max_minutes
                tvMins.text = "${state.data.max_minutes} Мин"
                tvMbValue.text = "${state.data.rate} Мб"
                etMin.filters = arrayOf(MinMaxFilterValue(0,state.data.max_minutes))
                exchangeInfo = state.data
            }

            is MinToGbState.IndicatorChange -> {
                minToGbSeekBar.progress = state.quantity
            }
            is MinToGbState.Loading -> {
                mainConstaint.visibility = View.GONE
                constraintLayout.visibility = View.GONE
                constraintLayout2.visibility = View.GONE
                pgLoading.visibility = View.VISIBLE
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
