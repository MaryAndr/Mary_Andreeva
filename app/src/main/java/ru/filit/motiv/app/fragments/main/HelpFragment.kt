package ru.filit.motiv.app.fragments.main

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment

import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.fragment_help.*
import ru.filit.motiv.app.R


class HelpFragment: Fragment(){



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.costs)))
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.abs_layout)
            setDisplayHomeAsUpEnabled(false)
            elevation = resources.getDimension(R.dimen.elevation)
        }

        activity!!.nav_view.visibility = View.VISIBLE
        val tvTitle: AppCompatTextView = activity!!.findViewById(R.id.tvTitle)
        tvTitle.setTextColor(resources.getColor(R.color.black))
        tvTitle.text = resources.getText(R.string.help)

        ll_questions_answers.setOnClickListener{
            val faqFragment = FaqFragment.newInstance()
            val fm = fragmentManager
            fm?.beginTransaction()
                ?.addToBackStack("help fragment")
                ?.replace(R.id.container, faqFragment)
                ?.commit()
        }

        ll_support.setOnClickListener {
            val supportFragment = SupportFragment()
            val fm = fragmentManager
            fm?.beginTransaction()
                ?.addToBackStack("help fragment")
                ?.replace(R.id.container, supportFragment)
                ?.commit()
        }

        ll_offices.setOnClickListener {
            val officesFragment = OfficesFragment.newInstance()
            val fm = fragmentManager
            fm?.beginTransaction()
                ?.addToBackStack("help fragment")
                ?.replace(R.id.container, officesFragment)
                ?.commit()
        }

    }
}