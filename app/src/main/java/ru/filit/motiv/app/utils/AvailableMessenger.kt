package ru.filit.motiv.app.utils

import android.content.Context
import android.content.pm.PackageManager

object AvailableMessenger {
    fun checkAvailableMessenger(url: String, ctx: Context?):Boolean{
        val packageManager = ctx?.packageManager
        try {
            packageManager?.getPackageInfo(url, PackageManager.GET_ACTIVITIES)
            return true
        }catch (e: PackageManager.NameNotFoundException){
            return false
        }
    }
    fun getListAvailableMessenger (ctx: Context?): List<String>{
        val listAvailableMessenger = arrayListOf<String>()
        if(checkAvailableMessenger(Constants.PACKAGE_NAME_VIBER, ctx)){
            listAvailableMessenger.add("viber")
        }
        if (checkAvailableMessenger(Constants.PACKAGE_NAME_WHATSAPP, ctx)){
            listAvailableMessenger.add("whatsapp")
        }
        return listAvailableMessenger
    }
}