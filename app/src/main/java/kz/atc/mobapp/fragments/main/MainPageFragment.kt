package kz.atc.mobapp.fragments.main

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

import kz.atc.mobapp.R
import kz.atc.mobapp.presenters.main.MainPagePresenter
import kz.atc.mobapp.states.main.MainPageState
import kz.atc.mobapp.views.main.MainPageView


class MainPageFragment : MviFragment<MainPageView, MainPagePresenter>(),
    MainPageView {
    override fun createPresenter() = MainPagePresenter(context!!)

    private lateinit var preLoadTrigger: BehaviorSubject<Int>

    override fun preLoadIntent(): Observable<Int> {
        return preLoadTrigger
    }

    override fun render(state: MainPageState) {
        when {
            state.mainDataLoaded -> {
                val phoneNumber = state.mainData?.phoneNumber
                val strTemp = "+7 ${phoneNumber?.substring(0,3)} ${phoneNumber?.substring(3,6)}-${phoneNumber?.substring(6,8)}-${phoneNumber?.substring(8,10)}"
                Toast.makeText(context!!, strTemp, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preLoadTrigger = BehaviorSubject.create()
    }

    override fun onResume() {
        super.onResume()
        preLoadTrigger.onNext(1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main_page, container, false)
    }

}
