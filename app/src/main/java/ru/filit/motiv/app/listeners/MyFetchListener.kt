package ru.filit.motiv.app.listeners

import android.content.Context
import android.widget.Toast
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2core.DownloadBlock

class MyFetchListener(val context: Context) : FetchListener{
    override fun onAdded(download: Download) {
    }

    override fun onCancelled(download: Download) {
    }

    override fun onCompleted(download: Download) {
        Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()

    }

    override fun onDeleted(download: Download) {
    }

    override fun onDownloadBlockUpdated(
        download: Download,
        downloadBlock: DownloadBlock,
        totalBlocks: Int
    ) {
    }

    override fun onError(download: Download, error: Error, throwable: Throwable?) {
        Toast.makeText(context, error.value, Toast.LENGTH_SHORT).show()
    }

    override fun onPaused(download: Download) {
    }

    override fun onProgress(
        download: Download,
        etaInMilliSeconds: Long,
        downloadedBytesPerSecond: Long
    ) {
    }

    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
    }

    override fun onRemoved(download: Download) {
    }

    override fun onResumed(download: Download) {
    }

    override fun onStarted(
        download: Download,
        downloadBlocks: List<DownloadBlock>,
        totalBlocks: Int
    ) {
        Toast.makeText(context, "Download Started", Toast.LENGTH_SHORT).show()
    }

    override fun onWaitingNetwork(download: Download) {
    }

}