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
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.fragment_block_unblock.*

import ru.filit.motiv.app.R
import ru.filit.motiv.app.dialogs.BlockUnblockDialog
import ru.filit.motiv.app.models.main.SettingsDataModel
import ru.filit.motiv.app.utils.TextConverter

class BlockUnblockFragment(var data: SettingsDataModel) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_block_unblock, container, false)

    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.costs)))
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.abs_layout)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_backbutton_black)
            elevation = resources.getDimension(R.dimen.elevation)
        }
        var tvTitle: AppCompatTextView = activity!!.findViewById(R.id.tvTitle)
        activity!!.nav_view.visibility = View.INVISIBLE
        tvTitle.setTextColor(resources.getColor(R.color.black))
        if (data.statusId == 1) {
            tvTitle.text = "Заблокировать номер"
        } else {
            tvTitle.text = "Разблокировать номер"
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawView()
    }

    private fun drawView() {
        if (data.msisdn != null) {
            tvPhoneNumber.text = TextConverter().getFormattedPhone(data.msisdn!!)
        } else {
            tvPhoneNumber.text = "N/A"
        }

        if (data.statusId == 1) {
            tvStatusMainBlock.text = "Блокировать номер"
            tvStatus.text = "Активен"
            ivLight.setImageResource(R.drawable.active_circle)
            tvStatusBlock.text = getString(R.string.BlockDefText)
            llReason.visibility = View.VISIBLE
            etReason.visibility = View.VISIBLE
            btnBlockUnlock.text = "Заблокировать номер"

            btnBlockUnlock.setOnClickListener {
                val aboutDialog = BlockUnblockDialog.newInstance(data, etReason.text.toString())
                aboutDialog.setTargetFragment(this, 1)
                aboutDialog.show(
                    activity!!.supportFragmentManager,
                    "settings_fragment"
                )
                aboutDialog
            }
        } else if (data.statusId == 4) {
            tvStatusMainBlock.text = "Разблокировка номера"
            tvStatus.text = "Приостановлен"
            ivLight.setImageResource(R.drawable.inactive_circle)
            tvStatusBlock.text = getString(R.string.UnblockDefText)
            llReason.visibility = View.GONE
            etReason.visibility = View.GONE
            btnBlockUnlock.text = "Разблокировать номер"

            btnBlockUnlock.setOnClickListener {
                val aboutDialog = BlockUnblockDialog.newInstance(data, null)
                aboutDialog.setTargetFragment(this, 1)
                aboutDialog.show(
                    activity!!.supportFragmentManager,
                    "settings_fragment"
                )
            }
        }

    }

}
