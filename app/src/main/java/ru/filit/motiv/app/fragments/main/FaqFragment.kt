package ru.filit.motiv.app.fragments.main

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.hannesdorfmann.mosby3.mvi.MviFragment
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.fragment_faq.*

import ru.filit.motiv.app.R
import ru.filit.motiv.app.presenters.main.FAQPresenter
import ru.filit.motiv.app.views.main.FAQView

class FaqFragment : MviFragment<FAQView, FAQPresenter>() {

    override fun createPresenter() = FAQPresenter(context!!)

    var myHtml =
        "<!-- STATIC HEADER START --><!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Help</title><style type=\"text/css\">* {-webkit-touch-callout: none;-webkit-user-select: none; /* Disable selection/copy in UIWebView */}</style><style>/* HTML5 display-role reset for older browsers */article, aside, details, figcaption, figure, footer, header, hgroup, menu, nav, section {display: block;}.container {color: #000;font-size: 16px;font-family: \"SF Pro Rounded, Helvetica Neue\", sans-serif;}.header {color: #000;font-size: 20px; font-family: \"SF Pro Rounded, Helvetica Neue\", sans-serif;} block_frame_orange {display: block; border-radius: 12px; border: 1.4px solid orange; margin: 0 em; margin-top: 1em; padding: 15px; } </style></head> <body> <!-- STATIC HEADER END --> <div class=\\\"header\\\">question from backend</div><div class=\\\"container\\\">answer from backend</div> <!-- STATIC TAIL START --></body></html> <!-- STATIC HEADER END -->"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val answer =
            "<p>Общайтесь на условиях тарифа домашнего региона в поездках по территории «Большого Урала» и принимайте входящие звонки по всей России бесплатно.</p> Чтобы снизить стоимость исходящих вызовов и услуг за пределами «Большого Урала», подключайте услугу «<a href=https://motivtelecom.ru/{region}/services/rouming-2-0> Роуминг 2.0.</a>».<br><b>Пример жирного текста</b><br><i>Пример курсива</i><br><block_frame_orange>Какой-то текст в оранжевом блоке и <b>выделением </b></block_frame_orange><br><ol><li>Пункт списка 1</li><li>Пункт списка 2</li></ol><br><u>Пример подчеркивания</u><br><ul><li>Маркированный список 1</li><li>Маркированный список 2</li></ul><br>"
        val question = "Как выгодно общаться в поездках по России?"
        myHtml = myHtml.replace("answer from backend", answer, true)
        myHtml = myHtml.replace("question from backend", question, true)

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
        }
        val tvTitle: AppCompatTextView = activity!!.findViewById(R.id.tvTitle)
        tvTitle.setTextColor(resources.getColor(R.color.black))
        tvTitle.text = getString(R.string.questions_and_answers)
        webview.loadDataWithBaseURL(null, myHtml, "text/html", "utf-8", null)
    }

    companion object {
        @JvmStatic
        fun newInstance() = FaqFragment()
    }


}
