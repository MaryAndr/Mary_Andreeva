package kz.atc.mobapp.utils

import android.util.Log
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class TimeUtils {

    private val mapOfMonth =
        mapOf(
            "0" to "января",
            "1" to "февраля",
            "2" to "марта",
            "3" to "апреля",
            "4" to "мая",
            "5" to "июня",
            "6" to "июля",
            "7" to "августа",
            "8" to "сентября",
            "9" to "октября",
            "10" to "ноября",
            "11" to "декабря"
        )

    fun secondsToString(pTime: Long): String {
        return String.format("%02d:%02d", pTime / 60, pTime % 60)
    }

    fun changeFormat(defDate: String, formatFrom: String, formatTo: String) : String? {

        val sdf = SimpleDateFormat(formatFrom)
        var convertedDate: Date? = null
        var formattedDate: String? = null
        try {
            convertedDate = sdf.parse(defDate)
            formattedDate = SimpleDateFormat(formatTo).format(convertedDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return formattedDate
    }

    fun debitDate(dateStr: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd")
            val cal = Calendar.getInstance()
            val date = parser.parse(dateStr)
            cal.time =  date
            Log.d("Debug", cal.get(Calendar.MONTH).toString())
            "Списание по тарифу ${cal.get(Calendar.DAY_OF_MONTH)} ${mapOfMonth[cal.get(Calendar.MONTH).toString()]}"
        } catch (ex: Exception) {
            "Некорректное значение"
        }
    }

    fun dateToString(cal: Calendar): String {
        val parser = SimpleDateFormat("dd.MM.yyyy")
        return parser.format(cal.time)
    }
}