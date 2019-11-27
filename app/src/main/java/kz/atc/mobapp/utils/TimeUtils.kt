package kz.atc.mobapp.utils

class TimeUtils {

   fun secondsToString(pTime: Long): String {
        return String.format("%02d:%02d", pTime / 60, pTime % 60)
    }
}