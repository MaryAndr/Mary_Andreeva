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

import ru.filit.motiv.app.R

class OfficesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_offices, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.costs)))
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.abs_layout)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_backbutton_black)
            elevation = resources.getDimension(R.dimen.elevation)
        }
        val tvTitle: AppCompatTextView = activity!!.findViewById(R.id.tvTitle)
        tvTitle.setTextColor(resources.getColor(R.color.black))
        tvTitle.text = getString(R.string.support)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            OfficesFragment()
    }
}
