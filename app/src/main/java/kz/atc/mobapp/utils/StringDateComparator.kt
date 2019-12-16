package kz.atc.mobapp.utils

import kz.atc.mobapp.models.main.SubPaymentsResponse
import java.lang.Exception
import java.text.SimpleDateFormat

class StringDateComparator : Comparator<SubPaymentsResponse> {
    private var dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    override fun compare(lhs: SubPaymentsResponse, rhs: SubPaymentsResponse): Int {
        return try {
            if (lhs == null || rhs == null) {
                0
            } else {
                dateFormat.parse(lhs.date).compareTo(dateFormat.parse(rhs.date))
            }
        } catch (ex: Exception) {
            0
        }
    }
}