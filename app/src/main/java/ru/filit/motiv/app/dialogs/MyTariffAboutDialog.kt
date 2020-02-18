package ru.filit.motiv.app.dialogs

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.my_tariff_about_dialog.*
import ru.filit.motiv.app.R
import ru.filit.motiv.app.adapters.InfoAdapter
import ru.filit.motiv.app.adapters.MyTariffAboutAdapter
import ru.filit.motiv.app.models.main.MyTariffAboutData
import ru.filit.motiv.app.utils.DownloadHelper
import ru.filit.motiv.app.utils.TextConverter
import ru.filit.motiv.app.models.catalogTariff.Attribute
import ru.filit.motiv.app.models.main.TariffDialogModelData
import ru.filit.motiv.app.utils.isConnect

//TODO: Необходимо переписать под MVI архитектуру, используя абстрактный класс BaseBottomDialogMVI
class MyTariffAboutDialog(
    val data: MyTariffAboutData,
    val isTariffChange: Boolean = false,
    val reloadTrigger: BehaviorSubject<Int>? = null
) :
    BottomSheetDialogFragment() {

    private lateinit var PDF_URL: String
    private lateinit var tariffName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (data.catalogTariff?.tariffs != null && data.catalogTariff.tariffs.isNotEmpty() && data.catalogTariff.tariffs.first()
                .attributes.firstOrNull { pred -> pred.system_name == "description_url" } != null
        ) {
            PDF_URL = data.catalogTariff.tariffs.first()
                .attributes.first { pred -> pred.system_name == "description_url" }.value
            tariffName = data.catalogTariff.tariffs.first().name
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

        if (!PDF_URL.contains(".pdf")) {
            pdfDownload.visibility = View.GONE
        }

        if (isTariffChange) {
            btnTariffChange.setOnClickListener {
                val dataToSend = TariffDialogModelData()
                dataToSend.tariffId = data.catalogTariff?.tariffs?.first()?.id?.toString()
                dataToSend.tariffName = data.catalogTariff?.tariffs?.first()?.name
                dataToSend.tariffAbonCost = data.catalogTariff?.tariffs?.first()
                    ?.attributes?.firstOrNull { it.system_name == "subscription_fee" }?.value
                dataToSend.tariffChangeCost = data.catalogTariff?.tariffs?.first()
                    ?.attributes?.firstOrNull { it.param == "Обязательный первичный платеж" }?.value
                if (reloadTrigger == null) {
                    Log.d("null", "is null")
                }
                val dialog =
                    TariffConfirmationDialogMVI.newInstance(dataToSend, reloadTrigger, this)
                dialog.show(
                    (context as AppCompatActivity).supportFragmentManager,
                    "Accept Dialog"
                )
        }
        } else {
            btnTariffChange.visibility = View.GONE
        }

        tvTariffName.text = "\"" + data.catalogTariff?.tariffs?.first()?.name + "\""

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
            if (!isConnect(context!!)) {
                val dialogBuilder = AlertDialog.Builder(this.context)
                dialogBuilder
                    .setMessage("Нет интернет соединения")
                    .setPositiveButton("OK") { _, _ ->
                    }
                    .create()
                    .show()
            } else {
                downloadPdf()
            }
        }

        if (data.subscriberTariff?.tariff?.constructor != null && isSelfTariff) {
            tvTariffDescription.text = TextConverter().descriptionBuilder(
                data.subscriberTariff.tariff.constructor.min,
                data.subscriberTariff.tariff.constructor.data,
                data.subscriberTariff.tariff.constructor.sms
            )
        } else {
            tvTariffDescription.text = data.catalogTariff?.tariffs?.first()
                ?.attributes?.firstOrNull { pred -> pred.system_name == "short_description" }
                ?.value.orEmpty()
        }

        if (isSubFee) {
            if (!attrs?.firstOrNull { it.system_name == "internet_gb_count" }?.value.orEmpty().isNullOrEmpty()) {
                tvAddData.text =
                    attrs?.firstOrNull { it.system_name == "internet_gb_count" }?.value.orEmpty() + " " + attrs?.firstOrNull { it.system_name == "internet_gb_count" }?.unit.orEmpty()
            } else {
                addDataView.visibility = View.GONE
            }
            if (!attrs?.firstOrNull { it.system_name == "minutes_count" }?.value.orEmpty().isNullOrEmpty()) {
                tvAddVoice.text =
                    attrs?.firstOrNull { it.system_name == "minutes_count" }?.value.orEmpty() + " Мин"
            } else {
                addVoiceView.visibility = View.GONE
            }
            if (!attrs?.firstOrNull { it.system_name == "sms_count" }?.value.orEmpty().isNullOrEmpty()) {
                tvAddSMS.text =
                    attrs?.firstOrNull { it.system_name == "sms_count" }?.value.orEmpty() + " SMS"
            } else {
                addSMSView.visibility = View.GONE
            }

            tvSubFee.apply {
                visibility = View.VISIBLE
                text =
                    "${attrs?.firstOrNull { it.system_name == "subscription_fee" }?.value} \u20BD/${getInterval()}"
            }
            tvSubFeeDisc.visibility = View.GONE
        } else {
            if (!attrs?.firstOrNull { it.system_name == "internet_mb_cost" }?.value.orEmpty().isNullOrEmpty()) {
                tvAddData.text =
                    attrs?.firstOrNull { it.system_name == "internet_mb_cost" }?.value.orEmpty() + " " + attrs?.firstOrNull { it.system_name == "internet_mb_cost" }?.unit.orEmpty()
            } else {
                addDataView.visibility = View.GONE
            }
            if (!attrs?.firstOrNull { it.system_name == "minute_cost" }?.value.orEmpty().isNullOrEmpty()) {
                tvAddVoice.text =
                    attrs?.firstOrNull { it.system_name == "minute_cost" }?.value.orEmpty() + attrs?.firstOrNull { it.system_name == "minute_cost" }?.unit.orEmpty()
            } else {
                addVoiceView.visibility = View.GONE
            }
            if (!attrs?.firstOrNull { it.system_name == "sms_cost" }?.value.orEmpty().isNullOrEmpty()) {
                tvAddSMS.text =
                    attrs?.firstOrNull { it.system_name == "sms_cost" }?.value.orEmpty() + attrs?.firstOrNull { it.system_name == "sms_cost" }?.unit.orEmpty()
            } else {
                addSMSView.visibility = View.GONE
            }

            tvSubFee.apply {
                visibility = View.VISIBLE
                text =
                    "${attrs?.firstOrNull { it.system_name == "subscription_fee" }?.value} \u20BD/${getInterval()}"
            }
        }

        if (isSelfTariff) {
            tvSubFee.visibility = View.VISIBLE

            if (data.subscriberTariff?.tariff?.constructor?.abon_discount != null && data.subscriberTariff.tariff.constructor.abon_discount != "0") {
                tvSubFeeDisc.visibility = View.VISIBLE
                tvSubFeeDisc.text =
                    "${data.subscriberTariff?.tariff?.constructor?.abon_discount} \u20BD/${getInterval()}"
                tvSubFee.apply {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    text =
                        "${data.subscriberTariff?.tariff?.constructor?.abon} \u20BD/${getInterval()}"
                }
            } else {
                tvSubFeeDisc.visibility = View.GONE
            }
            if (data.subscriberTariff?.tariff?.constructor?.abon != null) {
                tvSubFee.text =
                    "${data.subscriberTariff?.tariff?.constructor?.abon} \u20BD/${getInterval()}"
            } else {
                if (attrs?.firstOrNull { it.system_name == "subscription_fee" } != null && attrs.firstOrNull { it.system_name == "subscription_fee" }?.value != "0") {
                    tvSubFee.text =
                        "${attrs?.firstOrNull { it.system_name == "subscription_fee" }?.value} ₽/${getInterval()}"
                } else {
                    tvSubFee.visibility = View.GONE
                }
            }
            if (!data.subscriberTariff?.tariff?.constructor?.data.isNullOrEmpty()) {
                addDataView.visibility = View.VISIBLE
                tvAddData.text = "${data.subscriberTariff?.tariff?.constructor?.data} ГБ"
            } else {
                addDataView.visibility = View.GONE
            }

            if (!data.subscriberTariff?.tariff?.constructor?.min.isNullOrEmpty()) {
                addVoiceView.visibility = View.VISIBLE
                tvAddVoice.text = "${data.subscriberTariff?.tariff?.constructor?.min} Мин"
            } else {
                addVoiceView.visibility = View.GONE
            }

            if (!data.subscriberTariff?.tariff?.constructor?.sms.isNullOrEmpty()) {
                addSMSView.visibility = View.VISIBLE
                tvAddSMS.text = "${data.subscriberTariff?.tariff?.constructor?.sms} SMS"
            } else {
                addSMSView.visibility = View.GONE
            }

        } else {

            if (tvAddData.text.isNullOrEmpty() && tvAddVoice.text.isNullOrEmpty() && tvAddSMS.text.isNullOrEmpty()) {
                addHolder.visibility = View.GONE
            }
        }


        val attributesInfo = attrs?.filter { it.name == "Информация о тарифе" }

        if (attributesInfo != null && attributesInfo.isNotEmpty()) {
            infoList.layoutManager = LinearLayoutManager(context!!)
            infoList.adapter =
                InfoAdapter(context!!, attributesInfo)
        } else {
            tvTariffInformation.visibility = View.GONE
        }

        if (attrs?.firstOrNull { it.name == "Информация о тарифе" } != null) {
            tvAddInfoAbout.text = attrs.firstOrNull { it.name == "Информация о тарифе" }?.param
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

    private fun getInterval(): String {
        var interval = "месяц"
        data.catalogTariff?.tariffs?.forEach {
            if (data.subscriberTariff?.tariff?.name.equals(it.name)) {
                it.attributes.forEach { atribute ->
                    if (atribute.name.equals("периодичность списания АП**")) {
                        when (atribute.value) {
                            "Ежемесячно" -> interval = "месяц"
                            else -> interval = "сутки"
                        }
                    }
                }
            }
        }
        return interval

    }

    companion object {

        fun newInstance(
            data: MyTariffAboutData,
            isTariffChange: Boolean = false,
            reloadTrigger: BehaviorSubject<Int>? = null
        ): MyTariffAboutDialog {
            return MyTariffAboutDialog(data, isTariffChange, reloadTrigger)
        }
    }
}