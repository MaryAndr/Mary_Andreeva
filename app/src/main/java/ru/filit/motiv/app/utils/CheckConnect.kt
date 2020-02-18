package ru.filit.motiv.app.utils

import android.content.Context
import android.net.ConnectivityManager

fun isConnect(ctx: Context):Boolean{
    val connMgr =ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connMgr.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnectedOrConnecting
}