package kz.atc.mobapp.utils

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class DownloadReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)

        Toast.makeText(context, "Download Successful", Toast.LENGTH_SHORT).show()
        DownloadHelper().openDownloadedAttachment(context,id)
    }
}