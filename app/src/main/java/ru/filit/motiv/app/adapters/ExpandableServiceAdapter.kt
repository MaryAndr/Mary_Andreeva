package ru.filit.motiv.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ru.filit.motiv.app.R
import ru.filit.motiv.app.listeners.OnServiceToggleChangeListener
import ru.filit.motiv.app.models.main.ServicesListShow
import ru.filit.motiv.app.models.main.ToggleButtonState

class ExpandableServiceAdapter internal constructor(
    private val onToggleChangeListener: OnServiceToggleChangeListener
) : BaseExpandableListAdapter() {

    private var titleList: MutableList<String?> = mutableListOf()
    private var dataList: MutableMap<String, MutableList<ServicesListShow>> = mutableMapOf()

    fun setData(items:MutableList<ServicesListShow>){
        titleList.clear()
        dataList.clear()
        titleList = items.distinctBy { it.category }.map { it.category }.toMutableList()
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
            val layoutInflater = parent?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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
            val layoutInflater = parent?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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
        viewHolder.tvPriceRate?.text =child.price?.substringBefore(".0") + " Руб/" + child.interval

        when(child.toggleState) {
            ToggleButtonState.ActiveAndDisabled -> {
                viewHolder.tgService.isChecked = true
                viewHolder.tgService.isEnabled = false
            }
            ToggleButtonState.ActiveAndEnabled -> {
                viewHolder.tgService.isChecked = true
                viewHolder.tgService.isEnabled = true
                viewHolder.tgService.setOnCheckedChangeListener { compoundButton, isChecked ->
                    onToggleChangeListener.onToggleClick(child, isChecked, childPosition)}
            }
            ToggleButtonState.InactiveAndEnabled -> {
                viewHolder.tgService.isChecked = false
                viewHolder.tgService.isEnabled = true
                viewHolder.tgService.setOnCheckedChangeListener { compoundButton, isChecked ->
                    onToggleChangeListener.onToggleClick(child, isChecked, childPosition) }
            }
            else -> {viewHolder.itemView.visibility = View.GONE}
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