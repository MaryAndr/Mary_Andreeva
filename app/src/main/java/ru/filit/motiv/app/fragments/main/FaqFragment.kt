package ru.filit.motiv.app.fragments.main

import android.app.AlertDialog
import android.content.IntentFilter
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.fragment_faq.*

import ru.filit.motiv.app.R
import ru.filit.motiv.app.adapters.ExpandableCategoryQuestionsAdapter
import ru.filit.motiv.app.listeners.OnQuestionClickListener
import ru.filit.motiv.app.presenters.main.FAQPresenter
import ru.filit.motiv.app.states.main.FAQState
import ru.filit.motiv.app.utils.ConnectivityReceiver
import ru.filit.motiv.app.views.main.FAQView

class FaqFragment : MviFragment<FAQView, FAQPresenter>(), FAQView,
    ConnectivityReceiver.ConnectivityReceiverListener, OnQuestionClickListener {

    private var expandableCategoryList: List<Int>? = null

    private lateinit var preLoadTrigger: BehaviorSubject<Int>

    override fun createPresenter() = FAQPresenter(context!!)

    override fun getFAQIntent(): Observable<Int> {
        return preLoadTrigger
    }

    override fun render(state: FAQState) {
        when (state) {
            is FAQState.QuestionsLoaded -> {
                val expanableCategoryQuestionsAdapter = ExpandableCategoryQuestionsAdapter(this)
                rv_category_questions.setAdapter(expanableCategoryQuestionsAdapter)
                expanableCategoryQuestionsAdapter.setData(state.faq)
                if (expandableCategoryList != null) {
                    (expandableCategoryList as List).forEach { rv_category_questions.expandGroup(it) }
                }
                pgData.visibility = View.GONE
                rv_category_questions.visibility = View.VISIBLE
            }

            is FAQState.Loading -> {
                pgData.visibility = View.VISIBLE
                rv_category_questions.visibility = View.GONE
            }

            is FAQState.InternetState -> {
                if (state.active) {
                    rv_category_questions.visibility = View.VISIBLE
                    no_internet_view.visibility = View.GONE
                } else {
                    pgData.visibility = View.GONE
                    rv_category_questions.visibility = View.GONE
                    no_internet_view.visibility = View.VISIBLE
                }
            }

            is FAQState.ShowErrorMessage -> {
                val dialogBuilder = AlertDialog.Builder(this.context)
                dialogBuilder
                    .setMessage(state.message)
                    .setPositiveButton("OK") { _, _ ->
                    }
                    .create()
                    .show()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preLoadTrigger = BehaviorSubject.create()
        networkAvailabilityTrigger = BehaviorSubject.create()
        activity!!.registerReceiver(
            connectivityReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_faq, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.costs)))
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.abs_layout)
            elevation = resources.getDimension(R.dimen.elevation)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_backbutton_black)
            val tvTitle: AppCompatTextView? = activity?.findViewById(R.id.tvTitle)
            tvTitle?.text = getString(R.string.questions_and_answers)
            activity?.nav_view?.visibility = View.VISIBLE

        }

        if (savedInstanceState != null) {
            expandableCategoryList =
                savedInstanceState.getIntArray("expandable category Id list")?.toList()
        }

        preLoadTrigger.onNext(1)
    }

    companion object {
        @JvmStatic
        fun newInstance() = FaqFragment()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putIntArray("expandable category Id list", expandableCategoryList?.toIntArray())
        }
    }


    override fun onStop() {
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onClick(questionId: Int, expandableCategoriesId: List<Int>) {
        val frAnswer = AnswerFAQFragment.newInstance(questionId = questionId)
        val fm = activity!!.supportFragmentManager
        val fragmentTransaction = fm.beginTransaction().addToBackStack("faq")
        fragmentTransaction.add(R.id.container, frAnswer, "faq")
        fragmentTransaction.commit()
        this.expandableCategoryList = expandableCategoriesId
    }

    //check internet
    private val connectivityReceiver = ConnectivityReceiver()

    private lateinit var networkAvailabilityTrigger: BehaviorSubject<Boolean>

    override fun checkInternetConnectivityIntent(): Observable<Boolean> {
        return networkAvailabilityTrigger
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            networkAvailabilityTrigger.onNext(true)
        }
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(connectivityReceiver)
    }
}
