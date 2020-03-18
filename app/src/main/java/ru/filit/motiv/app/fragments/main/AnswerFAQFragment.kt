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
import kotlinx.android.synthetic.main.fragment_answer_faq.*
import ru.filit.motiv.app.R
import ru.filit.motiv.app.presenters.main.AnswerFAQPresenter
import ru.filit.motiv.app.states.main.AnswerFAQState
import ru.filit.motiv.app.utils.ConnectivityReceiver
import ru.filit.motiv.app.views.main.AnswerFAQView

private const val region = "region"
private const val question = "questionFAQ"

class AnswerFAQFragment: MviFragment<AnswerFAQView, AnswerFAQPresenter>(), AnswerFAQView,
    ConnectivityReceiver.ConnectivityReceiverListener {

    var myHtml =
        "<!-- STATIC HEADER START --><!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Help</title><style type=\"text/css\">* {-webkit-touch-callout: none;-webkit-user-select: none; /* Disable selection/copy in UIWebView */}</style><style>/* HTML5 display-role reset for older browsers */article, aside, details, figcaption, figure, footer, header, hgroup, menu, nav, section {display: block;}.container {color: #000;font-size: 16px;font-family: Roboto-Regular;}.header {color: #000;font-size: 30px; font-family: Roboto-Bold;} block_frame_orange {display: block; border-radius: 12px; border: 1.4px solid orange; margin: 5px; margin-top: 1em; padding: 15px; } </style></head> <body> <!-- STATIC HEADER END --> <div class=\\\"header\\\"><b>question from backend</b><br/></div><div class=\\\"container\\\">answer from backend</div> <!-- STATIC TAIL START --></body></html> <!-- STATIC HEADER END -->"

    private  var questionId: Int =0

    companion object {
        @JvmStatic
        fun newInstance(questionId: Int) = AnswerFAQFragment().apply {
            arguments = Bundle().apply {
                putInt(question, questionId)
            }
            this.questionId = questionId
        }
    }

    private lateinit var preLoadTrigger: BehaviorSubject<Int>

    override fun createPresenter() = AnswerFAQPresenter(context!!)

    override fun getAnswerIntent(): Observable<Int> {
        return preLoadTrigger
    }

    override fun render(state: AnswerFAQState) {
        when(state){
            is AnswerFAQState.QuestionsLoaded ->{
                webview.visibility = View.VISIBLE
                pgData.visibility = View.GONE
                webview.loadDataWithBaseURL(null,
                    getHtml(state.answerText,state.questionText),
                    "text/html",
                    "utf-8",
                    null)
            }
            is AnswerFAQState.Loading -> {
                pgData.visibility = View.VISIBLE
                webview.visibility = View.GONE
            }

            is AnswerFAQState.InternetState -> {
                if (state.active){
                    no_internet_view.visibility = View.GONE
                    webview.visibility = View.VISIBLE
                } else {
                    pgData.visibility = View.GONE
                    webview.visibility = View.GONE
                    no_internet_view.visibility = View.VISIBLE
                }
            }

            is AnswerFAQState.ShowErrorMessage -> {
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
        if (savedInstanceState!=null) {
            questionId =  savedInstanceState.getInt(question)
        }

        preLoadTrigger = BehaviorSubject.create()
        networkAvailabilityTrigger = BehaviorSubject.create()
        activity!!.registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_answer_faq, container, false)
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
        }
        val tvTitle: AppCompatTextView = activity!!.findViewById(R.id.tvTitle)
        tvTitle.setTextColor(resources.getColor(R.color.black))
        tvTitle.text = getString(R.string.question)
        activity!!.nav_view.visibility = View.INVISIBLE


        preLoadTrigger.onNext(questionId)
    }

    private fun getHtml(answer: String, question: String): String{
        myHtml = myHtml.replace("answer from backend", answer, true)
        myHtml = myHtml.replace("question from backend", question, true)
        return myHtml
    }

    //check internet
    private val connectivityReceiver = ConnectivityReceiver()

    private lateinit var networkAvailabilityTrigger : BehaviorSubject<Boolean>

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