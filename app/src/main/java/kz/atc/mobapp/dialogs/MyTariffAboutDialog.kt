package kz.atc.mobapp.dialogs

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Paint
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
import kz.atc.mobapp.listeners.MyFetchListener
import kz.atc.mobapp.models.catalogTariff.Attribute
import kz.atc.mobapp.models.catalogTariff.Tariff

//TODO: Необходимо переписать под MVI архитектуру, используя абстрактный класс BaseBottomDialogMVI
class MyTariffAboutDialog(val data: MyTariffAboutData) : BottomSheetDialogFragment() {

    private lateinit var PDF_URL: String
    private lateinit var tariffName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (data.catalogTariff?.tariffs != null && data.catalogTariff?.tariffs.isNotEmpty() && data.catalogTariff.tariffs.first()
                .attributes.firstOrNull { pred -> pred.system_name == "description_url" } != null
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

        val isSubFee = attrs?.firstOrNull { it.system_name == "subscription_fee" }?.value != "0"

        val isSelfTariff = data.subscriberTariff?.tariff?.id in mutableListOf(
            14,
            26,
            27,
            28
        )

        pdfDownload.setOnClickListener {
            downloadPdf()
        }

        if (data.subscriberTariff?.tariff?.constructor != null && isSelfTariff) {
            tvTariffDescription.text = TextConverter().descriptionBuilder(
                data.subscriberTariff?.tariff?.constructor?.min.substringBefore("."),
                data.subscriberTariff?.tariff?.constructor?.data,
                data.subscriberTariff?.tariff?.constructor?.sms.substringBefore(".")
            )
        } else {
            tvTariffDescription.text = data.catalogTariff?.tariffs?.first()
                ?.attributes?.firstOrNull { pred -> pred.system_name == "short_description" }
                ?.value.orEmpty()
        }

        if (isSubFee) {
            if (!attrs?.firstOrNull { it.system_name == "internet_gb_count" }?.value?.orEmpty().isNullOrEmpty()) {
                tvAddData.text =
                    attrs?.firstOrNull { it.system_name == "internet_gb_count" }?.value.orEmpty() + " " + attrs?.firstOrNull { it.system_name == "internet_gb_count" }?.unit.orEmpty()
            } else {
                tvAddData.text = null
            }
            if (!attrs?.firstOrNull { it.system_name == "minutes_count" }?.value?.orEmpty().isNullOrEmpty()) {
                tvAddVoice.text =
                    attrs?.firstOrNull { it.system_name == "minutes_count" }?.value.orEmpty() + " Мин"
            } else {
                tvAddVoice.text = null
            }
            if (!attrs?.firstOrNull { it.system_name == "sms_count" }?.value?.orEmpty().isNullOrEmpty()) {
                tvAddSMS.text =
                    attrs?.firstOrNull { it.system_name == "sms_count" }?.value.orEmpty() + " SMS"
            } else {
                tvAddSMS.text = null
            }
        } else {
            if (!attrs?.firstOrNull { it.system_name == "internet_mb_cost" }?.value?.orEmpty().isNullOrEmpty()) {
                tvAddData.text =
                    attrs?.firstOrNull { it.system_name == "internet_mb_cost" }?.value.orEmpty() + " " + attrs?.firstOrNull { it.system_name == "internet_mb_cost" }?.unit.orEmpty()
            } else {
                tvAddData.text = null
            }
            if (!attrs?.firstOrNull { it.system_name == "minute_cost" }?.value?.orEmpty().isNullOrEmpty()) {
                tvAddVoice.text =
                    attrs?.firstOrNull { it.system_name == "minute_cost" }?.value.orEmpty() + attrs?.firstOrNull { it.system_name == "minute_cost" }?.unit.orEmpty()
            } else {
                tvAddVoice.text = null
            }
            if (!attrs?.firstOrNull { it.system_name == "sms_cost" }?.value?.orEmpty().isNullOrEmpty()) {
                tvAddSMS.text =
                    attrs?.firstOrNull { it.system_name == "sms_cost" }?.value.orEmpty() + attrs?.firstOrNull { it.system_name == "sms_cost" }?.unit.orEmpty()
            } else {
                tvAddSMS.text = null
            }
        }

        if (isSelfTariff) {

            if (data.subscriberTariff?.tariff?.constructor?.abon_discount != null && data.subscriberTariff?.tariff?.constructor?.abon_discount != "0") {
                tvSubFeeDisc.visibility = View.VISIBLE
                tvSubFeeDisc.text =
                    data.subscriberTariff?.tariff?.constructor?.abon_discount.toString() + " руб."
                tvSubFee.apply {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    text = data.subscriberTariff?.tariff?.constructor?.abon + " руб."
                }
            } else {
                tvSubFee.text = data.subscriberTariff?.tariff?.constructor?.abon + " руб."
                tvSubFeeDisc.visibility = View.GONE
            }

        } else if (attrs?.firstOrNull { it.system_name == "subscription_fee" } != null && attrs?.firstOrNull { it.system_name == "subscription_fee" }?.value != "0") {
            tvSubFee.text =
                attrs?.firstOrNull { it.system_name == "subscription_fee" }?.value + " " + attrs?.firstOrNull { it.system_name == "subscription_fee" }?.unit
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

        if (tvAddData.text.isNullOrEmpty() && tvAddVoice.text.isNullOrEmpty() && tvAddSMS.text.isNullOrEmpty()) {
            addHolder.visibility = View.GONE
        }


        val attributesInfo = attrs?.filter { it.name == "Информация о тарифе" }

        if (attributesInfo != null && attributesInfo.isNotEmpty()) {
            infoList.layoutManager = LinearLayoutManager(context!!)
            infoList.adapter =
                InfoAdapter(context!!, attributesInfo)
        }

        if (attrs?.firstOrNull { it.name == "Информация о тарифе" } != null) {
            tvAddInfoAbout.text = attrs?.firstOrNull { it.name == "Информация о тарифе" }?.param
            tvSubscriberFee.text =
                attrs?.firstOrNull { it.name == "Информация о тарифе" }?.value.orEmpty() + attrs?.firstOrNull { it.name == "Информация о тарифе" }?.unit.orEmpty()

            Log.d("TvAddInfoAbout", tvAddInfoAbout.text.toString())
        } else {
            tvAddInfoAbout.visibility = View.GONE
            tvSubscriberFee.visibility = View.GONE
            viewUnderAddInfo.visibility = View.GONE
        }
        val tempList = data.catalogTariff?.tariffs?.firstOrNull()?.attributes?.toMutableList()
        if (isSelfTariff) {
            if (data.subscriberTariff?.tariff?.constructor?.min != null && data.subscriberTariff?.tariff?.constructor?.min != "0") {
                tempList?.add(
                    Attribute(
                        0,
                        "Включено в абонентскую плату",
                        "",
                        "Исходящие звонки на номера РФ",
                        "",
                        "мин",
                        data.subscriberTariff?.tariff?.constructor?.min
                    )
                )
            }
            if (data.subscriberTariff?.tariff?.constructor?.data != null && data.subscriberTariff?.tariff?.constructor?.data != "0") {
                tempList?.add(
                    Attribute(
                        0,
                        "Включено в абонентскую плату",
                        "",
                        "Мобильный интернет на максимальной скорости",
                        "",
                        "ГБ",
                        data.subscriberTariff?.tariff?.constructor?.data
                    )
                )
            }
            if (data.subscriberServices?.firstOrNull { pred -> pred.id == 1791 } != null) {
                tempList?.add(
                    Attribute(
                        0,
                        "Включено в абонентскую плату",
                        "",
                        "Безлимит на соц.сети\t",
                        "",
                        "",
                        "Предоставляется"
                    )
                )
            }
            if (data.subscriberTariff?.tariff?.constructor?.sms != null && data.subscriberTariff?.tariff?.constructor?.sms != "0") {
                tempList?.add(
                    Attribute(
                        0,
                        "Включено в абонентскую плату",
                        "",
                        "Исходящие SMS-сообщения на номера РФ",
                        "",
                        "SMS",
                        data.subscriberTariff?.tariff?.constructor?.sms
                    )
                )
            }
        }

        if (data.catalogTariff != null) {
            paramsRView.layoutManager = LinearLayoutManager(context!!)
            paramsRView.adapter =
                MyTariffAboutAdapter(tempList!!, context!!)
        } else {
            paramsRView.visibility = View.GONE
        }
    }


    private fun downloadPdf() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity!!.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                requestPermissions(
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