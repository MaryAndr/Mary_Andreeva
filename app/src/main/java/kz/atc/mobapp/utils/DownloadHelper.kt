package kz.atc.mobapp.utils

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.webkit.URLUtil




class DownloadHelper {

    val permissionCode = 1000


    fun performDownload(url: String, tariffName: String, activity: Activity) {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setTitle("Скачивание")
            request.setDescription("Файл скачивается...")
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setMimeType("application/pdf")
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "$tariffName.pdf"
            )
            Log.d("DM", url)
            val manager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            manager.enqueue(request)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}