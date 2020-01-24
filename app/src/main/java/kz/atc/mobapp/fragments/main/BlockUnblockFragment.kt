package kz.atc.mobapp.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.fragment_block_unblock.*

import kz.atc.mobapp.R
import kz.atc.mobapp.dialogs.BlockUnblockDialog
import kz.atc.mobapp.models.main.SettingsDataModel
import kz.atc.mobapp.utils.TextConverter

class BlockUnblockFragment(var data: SettingsDataModel) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_block_unblock, container, false)

    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_backbutton_black)
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
