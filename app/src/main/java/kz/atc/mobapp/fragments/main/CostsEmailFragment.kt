package kz.atc.mobapp.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_costs_email.*

import kz.atc.mobapp.R
import kz.atc.mobapp.presenters.main.CostsEmailPresenter
import kz.atc.mobapp.states.main.CostsEmailState
import kz.atc.mobapp.views.main.CostsEmailView


class CostsEmailFragment :
    MviFragment<CostsEmailView, CostsEmailPresenter>(),
    CostsEmailView  {

    private lateinit var msisdnLoadTrigger: BehaviorSubject<Int>

    override fun msisdnLoadIntent(): Observable<Int> {
        return msisdnLoadTrigger
    }

    override fun sendEmailIntent(): Observable<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun render(state: CostsEmailState) {
        when(state) {
            is CostsEmailState.MsisdnShown -> {
                tvPhoneNumber.text = state.msisdn
            }
        }
    }

    override fun createPresenter() = CostsEmailPresenter(context!!)

    override fun onResume() {
        super.onResume()
        msisdnLoadTrigger.onNext(1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        msisdnLoadTrigger = BehaviorSubject.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_costs_email, container, false)
    }
}
