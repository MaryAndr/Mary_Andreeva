package kz.atc.mobapp.dialogs

import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.my_tariff_about_dialog.*
import kz.atc.mobapp.R
import kz.atc.mobapp.adapters.MyTariffAboutAdapter
import kz.atc.mobapp.models.main.MyTariffAboutData


class MyTariffAboutDialog(val data: MyTariffAboutData) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

// get the views and attach the listener

        return inflater.inflate(
            R.layout.my_tariff_about_dialog, container,
            false
        )

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(data.catalogTariff != null) {
            paramsRView.layoutManager = LinearLayoutManager(context!!)
            paramsRView.adapter =
                MyTariffAboutAdapter(data.catalogTariff, context!!)
        } else {
            paramsRView.visibility = View.GONE
        }
    }

    companion object {

        fun newInstance(data: MyTariffAboutData): MyTariffAboutDialog {
            return MyTariffAboutDialog(data)
        }
    }
}