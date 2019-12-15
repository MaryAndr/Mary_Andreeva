package kz.atc.mobapp.utils

import android.content.Context
import android.util.Log
import android.widget.TextView
import ru.slybeaver.slycalendarview.SlyCalendarDialog
import java.text.SimpleDateFormat
import java.util.*

class CalendarView(val tv: TextView) : SlyCalendarDialog.Callback{

    override fun onDataSelected(startDate: Calendar?, endDate: Calendar?, p2: Int, p3: Int) {
        if (startDate != null) {
            Log.d("Calendar",
                SimpleDateFormat("EE, dd MMM", Locale.getDefault()).format(
                    startDate.time
                ))
            tv.text = TimeUtils().dateToString(startDate)
        }
        if (endDate != null) {
            Log.d("Calendar",
                SimpleDateFormat("EE, dd MMM", Locale.getDefault()).format(
                    endDate.time
                ))
            if (startDate != null) {
                tv.text = "${tv.text}-${TimeUtils().dateToString(endDate)}"
            } else {
                tv.text = TimeUtils().dateToString(endDate)
            }
        }
    }

    override fun onCancelled() {
    }

}