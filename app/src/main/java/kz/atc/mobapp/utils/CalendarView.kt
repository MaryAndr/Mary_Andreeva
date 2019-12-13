package kz.atc.mobapp.utils

import android.util.Log
import ru.slybeaver.slycalendarview.SlyCalendarDialog
import java.text.SimpleDateFormat
import java.util.*

class CalendarView : SlyCalendarDialog.Callback{

    override fun onDataSelected(startDate: Calendar?, endDate: Calendar?, p2: Int, p3: Int) {
        if (startDate != null) {
            Log.d("Calendar",
                SimpleDateFormat("EE, dd MMM", Locale.getDefault()).format(
                    startDate.time
                ))
        }
        if (endDate != null) {
            Log.d("Calendar",
                SimpleDateFormat("EE, dd MMM", Locale.getDefault()).format(
                    endDate.time
                ))
        }
    }

    override fun onCancelled() {
    }

}