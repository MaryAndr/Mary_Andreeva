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
import kz.atc.mobapp.adapters.MyTariffAboutAdapter
import kz.atc.mobapp.models.main.MyTariffAboutData
import kz.atc.mobapp.utils.DownloadHelper


class MyTariffAboutDialog(val data: MyTariffAboutData) : BottomSheetDialogFragment() {

    private lateinit var PDF_URL: String
    private lateinit var tariffName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (data.catalogTariff?.tariffs != null && data.catalogTariff?.tariffs.size > 0 && data.catalogTariff?.tariffs.first()
                .attributes.first { pred -> pred.system_name == "description_url" } != null
        ) {
            PDF_URL = data.catalogTariff.tariffs.first()
                .attributes.first { pred -> pred.system_name == "description_url" }.value
            tariffName = data.catalogTariff.tariffs?.first().name
        }
        else {
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

        pdfDownload.setOnClickListener {
            Log.d("OnClick", "Triggered")
            downloadPdf()
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