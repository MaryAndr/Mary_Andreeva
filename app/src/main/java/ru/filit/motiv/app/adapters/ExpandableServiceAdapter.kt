package ru.filit.motiv.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import ru.filit.motiv.app.R
import ru.filit.motiv.app.dialogs.ServiceConfirmationDialogMVI
import ru.filit.motiv.app.listeners.OnServiceToggleChangeListner
import ru.filit.motiv.app.models.main.ServiceDialogModel
import ru.filit.motiv.app.models.main.ServicesListShow
import ru.filit.motiv.app.models.main.ToggleButtonState
import ru.filit.motiv.app.utils.TimeUtils
import java.util.*

class ExpandableServiceAdapter internal constructor(
    private val context: Context,

    private val onToggleChangeListener: OnServiceToggleChangeListner
) : BaseExpandableListAdapter() {

    private var titleList: List<String?> = mutableListOf()
    private var dataList: MutableMap<String, MutableList<ServicesListShow>> = mutableMapOf()

    fun setData(items:MutableList<ServicesListShow>){
        titleList = items.distinctBy { it.category }.map { it.category }
        dataList = mutableMapOf<String, MutableList<ServicesListShow>>()
        titleList.forEach{title ->
            dataList[title!!] = items.filter {it.category == title}.toMutableList()
        }
        notifyDataSetChanged()
    }
    override fun getGroup(groupPosition: Int): String {
        return titleList[groupPosition]!!
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val viewHolder: ExpandableServiceAdapter.GroupViewHolder
        val rowView: View?
        val groupTitle = getGroup(groupPosition)
        val groupCount = dataList[groupTitle]?.size
        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowView = layoutInflater.inflate(R.layout.services_group_item, null)
            viewHolder = GroupViewHolder(rowView)
            rowView.tag = viewHolder
        }else {
            rowView = convertView
            viewHolder = rowView.tag as GroupViewHolder
        }

        if (isExpanded) {
            viewHolder.imgIndicator.setImageResource(R.drawable.ic_arrowup)
        } else {
            viewHolder.imgIndicator.setImageResource(R.drawable.ic_arrowdown)
        }

        viewHolder.tvCount.text = groupCount.toString()
        viewHolder.tvGroupName.text = groupTitle
        return rowView as View

    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return dataList[titleList[groupPosition]]!!.size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): ServicesListShow {
        return dataList[titleList[groupPosition]]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val viewHolder: ServicesViewHolder
        val rowView: View?
        val child = getChild(groupPosition, childPosition)

        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowView = layoutInflater.inflate(R.layout.services_item, null)
            viewHolder = ServicesViewHolder(rowView)
            rowView.tag = viewHolder
        }else {
            rowView = convertView
            viewHolder = rowView.tag as ServicesViewHolder
        }

        viewHolder.tgService.setOnCheckedChangeListener(null)
        viewHolder.tvInfoName?.text = child.serviceName
        viewHolder.tvDescription?.text = child.description
        viewHolder.tvPriceRate?.text = child.price + "/" + child.interval

        when(child.toggleState) {
            ToggleButtonState.ActiveAndDisabled -> {
                viewHolder.tgService.isChecked = true
                viewHolder.tgService.isEnabled = false
                viewHolder.tgService.setBackgroundResource(R.drawable.toggle_dis_on)
            }
            ToggleButtonState.ActiveAndEnabled -> {
                viewHolder.tgService.isChecked = true
                viewHolder.tgService.isEnabled = true
                viewHolder.tgService.setOnCheckedChangeListener { compoundButton, isChecked ->
                    onToggleChangeListener.onToggleClick(child, isChecked)}
            }
            ToggleButtonState.InactiveAndEnabled -> {
                viewHolder.tgService.isChecked = false
                viewHolder.tgService.isEnabled = true
                viewHolder.tgService.setOnCheckedChangeListener { compoundButton, isChecked ->
                    onToggleChangeListener.onToggleClick(child, isChecked) }
            }
        }

        return rowView as View

    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return titleList.size
    }

    private class GroupViewHolder(view: View?) {
        val tvGroupName = view?.findViewById(R.id.tvGroupName) as TextView
        val tvCount = view?.findViewById(R.id.tvCount) as TextView
        val imgIndicator = view?.findViewById(R.id.expandImg) as ImageView
    }

}