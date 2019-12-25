package kz.atc.mobapp.dialogs

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.my_tariff_about_dialog.*
import kz.atc.mobapp.R
import kz.atc.mobapp.adapters.InfoAdapter
import kz.atc.mobapp.adapters.MyTariffAboutAdapter
import kz.atc.mobapp.models.main.MyTariffAboutData
import kz.atc.mobapp.utils.DownloadHelper
import kz.atc.mobapp.utils.TextConverter


class MyTariffAboutDialog(val data: MyTariffAboutData) : BottomSheetDialogFragment() {

    private lateinit var PDF_URL: String
    private lateinit var tariffName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (data.catalogTariff?.tariffs != null && data.catalogTariff?.tariffs.size > 0 && data.catalogTariff.tariffs.first()
                .attributes.first { pred -> pred.system_name == "description_url" } != null
        ) {
            PDF_URL = data.catalogTariff.tariffs.first()
                .attributes.first { pred -> pred.system_name == "description_url" }.value
            tariffName = data.catalogTariff.tariffs?.first().name
        } else {
            PDF_URL = ""
            tariffName = "defaultName"
        }
    }


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


        tvTariffName.text = data.catalogTariff?.tariffs?.first()?.name

        ivClose.setOnClickListener {
            dismiss()
        }

        val attrs = data.catalogTariff?.tariffs?.first()?.attributes

        val isSubFee = attrs?.firstOrNull{it.system_name == "subscription_fee"}?.value == "1"

        pdfDownload.setOnClickListener {
            Log.d("OnClick", "Triggered")
            downloadPdf()
        }

        if (data.subscriberTariff?.constructor != null && data.subscriberTariff.tariff.id in mutableListOf(14, 26, 27, 28) ) {
            tvTariffDescription.text = TextConverter().descriptionBuilder(
                data.subscriberTariff?.constructor?.min,
                data.subscriberTariff?.constructor?.data,
                data.subscriberTariff?.constructor?.sms
            )
        } else {
            tvTariffDescription.text = data.catalogTariff?.tariffs?.first()
                ?.attributes?.firstOrNull { pred -> pred.system_name == "short_description" }
                ?.value.orEmpty()
        }

        if (isSubFee) {
            tvAddData.text = attrs?.firstOrNull{it.system_name == "internet_gb_count"}?.value.orEmpty() + attrs?.firstOrNull{it.system_name == "internet_gb_count"}?.unit.orEmpty()
            tvAddVoice.text = attrs?.firstOrNull{it.system_name == "minutes_count"}?.value.orEmpty() + attrs?.firstOrNull{it.system_name == "minutes_count"}?.unit.orEmpty()
            tvAddSMS.text = attrs?.firstOrNull{it.system_name == "sms_count"}?.value.orEmpty() + attrs?.firstOrNull{it.system_name == "sms_count"}?.unit.orEmpty()
        } else {
            tvAddData.text = attrs?.firstOrNull{it.system_name == "internet_mb_cost"}?.value.orEmpty() + attrs?.firstOrNull{it.system_name == "internet_mb_cost"}?.unit.orEmpty()
            tvAddVoice.text = attrs?.firstOrNull{it.system_name == "minute_cost"}?.value.orEmpty() + attrs?.firstOrNull{it.system_name == "minute_cost"}?.unit.orEmpty()
            tvAddSMS.text = attrs?.firstOrNull{it.system_name == "sms_cost"}?.value.orEmpty() + attrs?.firstOrNull{it.system_name == "sms_cost"}?.unit.orEmpty()
        }

        if (attrs?.firstOrNull{it.system_name == "subscription_fee"} != null && attrs?.firstOrNull{it.system_name == "subscription_fee"}?.value != "0") {
            tvSubFee.text = attrs?.firstOrNull{it.system_name == "subscription_fee"}?.value + " " + attrs?.firstOrNull{it.system_name == "subscription_fee"}?.unit
        } else {
            tvSubFee.visibility = View.GONE
        }

        if (tvAddData.text.isNullOrEmpty()) {
            addDataView.visibility = View.GONE
        }

        if (tvAddVoice.text.isNullOrEmpty()) {
            addVoiceView.visibility = View.GONE
        }

        if (tvAddSMS.text.isNullOrEmpty()) {
            addSMSView.visibility = View.GONE
        }

        val attributesInfo = attrs?.filter{it.name == "Информация о тарифе"}

        if (attributesInfo != null && attributesInfo.isNotEmpty()) {
            infoList.layoutManager = LinearLayoutManager(context!!)
            infoList.adapter =
                InfoAdapter(context!!, attributesInfo)
        }

        if (attrs?.firstOrNull{it.name == "Информация о тарифе"} != null) {
            tvAddInfoAbout.text = attrs?.firstOrNull{it.name == "Информация о тарифе"}?.param
            tvSubscriberFee.text = attrs?.firstOrNull{it.name == "Информация о тарифе"}?.value.orEmpty() + attrs?.firstOrNull{it.name == "Информация о тарифе"}?.unit.orEmpty()

            Log.d("TvAddInfoAbout", tvAddInfoAbout.text.toString())
        } else {
            tvAddInfoAbout.visibility = View.GONE
            tvSubscriberFee.visibility = View.GONE
            viewUnderAddInfo.visibility = View.GONE
        }

        if (data.catalogTariff != null) {
            paramsRView.layoutManager = LinearLayoutManager(context!!)
            paramsRView.adapter =
                MyTariffAboutAdapter(data.catalogTariff, context!!)
        } else {
            paramsRView.visibility = View.GONE
        }
    }

    private fun downloadPdf() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity!!.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                activity!!.requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    DownloadHelper().permissionCode
                )
            } else {
                DownloadHelper().performDownload(PDF_URL, tariffName, activity!!)
            }
        } else {
            DownloadHelper().performDownload(PDF_URL, tariffName, activity!!)

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            DownloadHelper().permissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DownloadHelper().performDownload(PDF_URL, tariffName, activity!!)
                } else {
                    Toast.makeText(
                        context,
                        "Вы не дали разрешение, файл не будет скачан.",
                        Toast.LENGTH_LONG
                    )
                }
            }
        }
    }

    companion object {

        fun newInstance(data: MyTariffAboutData): MyTariffAboutDialog {
            return MyTariffAboutDialog(data)
        }
    }
}