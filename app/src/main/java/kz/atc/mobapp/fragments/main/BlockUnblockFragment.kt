package kz.atc.mobapp.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_block_unblock.*

import kz.atc.mobapp.R
import kz.atc.mobapp.models.main.SettingsDataModel
import kz.atc.mobapp.utils.TextConverter

/**
 * A simple [Fragment] subclass.
 */
class BlockUnblockFragment(val data: SettingsDataModel) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_block_unblock, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawView(data)
    }

    private fun drawView(drawData: SettingsDataModel) {
        if (drawData.msisdn != null) {
            tvPhoneNumber.text = TextConverter().getFormattedPhone(drawData.msisdn)
        } else {
            tvPhoneNumber.text = "N/A"
        }

        if (drawData.statusId == 1) {
            tvStatusMainBlock.text = "Блокировать номер"
            tvStatus.text = "Активен"
            ivLight.setImageResource(R.drawable.active_circle)
            tvStatusBlock.text = getString(R.string.BlockDefText)
            llReason.visibility = View.VISIBLE
            etReason.visibility = View.VISIBLE
            btnBlockUnlock.text = "Заблокировать номер"
        } else if (drawData.statusId == 4) {
            tvStatusMainBlock.text = "Разблокировка номера"
            tvStatus.text = "Приостановлен"
            ivLight.setImageResource(R.drawable.inactive_circle)
            tvStatusBlock.text = getString(R.string.UnblockDefText)
            llReason.visibility = View.VISIBLE
            etReason.visibility = View.VISIBLE
            btnBlockUnlock.text = "Заблокировать номер"
        }
    }

}
