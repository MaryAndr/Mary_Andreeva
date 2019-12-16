package kz.atc.mobapp.adapters

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import kz.atc.mobapp.R
import kz.atc.mobapp.models.main.SubPaymentsResponse
import kz.atc.mobapp.utils.TimeUtils

class RepAdapter(var context: Context, var payments: List<SubPaymentsResponse>) : BaseAdapter() {

    private val layoutInflater = LayoutInflater.from(context)

    private var currentMonth: String = ""

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val rowView: View?

        if (convertView == null) {
            rowView = layoutInflater.inflate(R.layout.rep_list_view, parent, false)

            viewHolder = ViewHolder(rowView)
            rowView.tag = viewHolder

        } else {
            rowView = convertView
            viewHolder = rowView.tag as ViewHolder
        }
        Log.d("count", payments.size.toString())
        val payment = getItem(position)

        val mainHolderParam = viewHolder.mainHolder.layoutParams as LinearLayout.LayoutParams

        if (currentMonth != TimeUtils().getMonthAndYearFromDate(payment.date)) {
            currentMonth = TimeUtils().getMonthAndYearFromDate(payment.date)
            mainHolderParam.setMargins(0, TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16f,
                context.resources.displayMetrics
            ).toInt(),0,0)
            viewHolder.mainHolder.layoutParams = mainHolderParam
            viewHolder.monthDivider.visibility = View.VISIBLE
            viewHolder.monthDivider.text = currentMonth
        } else {
            viewHolder.monthDivider.visibility = View.GONE
            mainHolderParam.setMargins(0, 0,0,0)
            viewHolder.mainHolder.layoutParams = mainHolderParam
        }

        viewHolder.date.text = TimeUtils().getDateForListView(payment.date)
        viewHolder.gateway.text = payment.gateway
        viewHolder.sum.text = payment.amount.toString()

        return rowView as View
    }

    override fun getItem(position: Int): SubPaymentsResponse {
        return payments[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {

        return payments.size
    }

    private class ViewHolder(view: View?) {
        val monthDivider = view?.findViewById(R.id.tvMonthDivider) as TextView
        val gateway = view?.findViewById(R.id.tvGateway) as TextView
        val date = view?.findViewById(R.id.tvDate) as TextView
        val sum = view?.findViewById(R.id.tvSum) as TextView
        val mainHolder = view?.findViewById(R.id.lvMainHolder) as LinearLayout
    }

}