package ru.filit.motiv.app.utils

import android.util.Log
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
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

    private val mapOfMonthOriginal =
        mapOf(
            "0" to "Январь",
            "1" to "Февраль",
            "2" to "Март",
            "3" to "Апрель",
            "4" to "Май",
            "5" to "Июнь",
            "6" to "Июль",
            "7" to "Август",
            "8" to "Сентябрь",
            "9" to "Октябрь",
            "10" to "Ноябрь",
            "11" to "Декабрь"
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

    fun getMonthAndYearFromDate(date: String): String {
        val d = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)
        val cal = Calendar.getInstance()
        cal.time = d
        return "${mapOfMonthOriginal[cal.get(Calendar.MONTH).toString()]}, ${cal.get(Calendar.YEAR)}"
    }

    fun getDateForListView(date: String) : String {
        val d = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)
        return  SimpleDateFormat("dd.MM.yyyy',' HH:mm").format(d)
    }

    fun dateToString(cal: Calendar): String {
        val parser = SimpleDateFormat("dd.MM.yyyy")
        return parser.format(cal.time)
    }

    fun returnPeriodMinusThreeMonth(): String {
        val threeMonthAgo = Calendar.getInstance()
        threeMonthAgo.add(Calendar.MONTH, -3)
        return "${TimeUtils().dateToString(threeMonthAgo)}-${TimeUtils().dateToString(Calendar.getInstance())}"
    }
}