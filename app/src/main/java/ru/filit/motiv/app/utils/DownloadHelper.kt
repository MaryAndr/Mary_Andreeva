package ru.filit.motiv.app.utils

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.core.content.FileProvider
import android.content.ContentResolver
import java.io.File


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

    fun openDownloadedAttachment(context: Context, downloadId: Long) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        query.setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            val downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            val downloadLocalUri =
                cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
            val downloadMimeType =
                cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE))
            if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL && downloadLocalUri != null) {
                openDownloadedAttachment(context, Uri.parse(downloadLocalUri), downloadMimeType)
            }
        }
        cursor.close()
    }

    /**
     * Used to open the downloaded attachment.
     *
     *
     * 1. Fire intent to open download file using external application.
     *
     * 2. Note:
     * 2.a. We can't share fileUri directly to other application (because we will get FileUriExposedException from Android7.0).
     * 2.b. Hence we can only share content uri with other application.
     * 2.c. We must have declared FileProvider in manifest.
     * 2.c. Refer - https://developer.android.com/reference/android/support/v4/content/FileProvider.html
     *
     * @param context            Context.
     * @param attachmentUri      Uri of the downloaded attachment to be opened.
     * @param attachmentMimeType MimeType of the downloaded attachment.
     */
    private fun openDownloadedAttachment(
        context: Context,
        attachmentUri: Uri?,
        attachmentMimeType: String
    ) {
        var attachmentUri = attachmentUri
        if (attachmentUri != null) {
            // Get Content Uri.
            if (ContentResolver.SCHEME_FILE == attachmentUri.scheme) {
                // FileUri - Convert it to contentUri.
                val file = File(attachmentUri.path)
                attachmentUri =
                    FileProvider.getUriForFile(context, "ru.filit.motiv.provider", file)
            }

            val openAttachmentIntent = Intent(Intent.ACTION_VIEW)
            openAttachmentIntent.setDataAndType(attachmentUri, attachmentMimeType)
            openAttachmentIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            openAttachmentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(openAttachmentIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    "Невозможно открыть файл",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }
}