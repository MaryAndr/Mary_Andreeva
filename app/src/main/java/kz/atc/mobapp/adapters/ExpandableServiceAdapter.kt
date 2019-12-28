package kz.atc.mobapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ToggleButton
import kotlinx.android.synthetic.main.services_item.view.*
import kz.atc.mobapp.R
import kz.atc.mobapp.models.main.ServicesListShow

class ExpandableServiceAdapter internal constructor(
    private val context: Context,
    private val titleList: List<String?>,
    private val dataList: MutableMap<String, MutableList<ServicesListShow>>
) : BaseExpandableListAdapter() {
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
        val viewHolder: ExpandableServiceAdapter.ChildViewHolder
        val rowView: View?
        val child = getChild(groupPosition, childPosition)
        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowView = layoutInflater.inflate(R.layout.services_item, null)
            viewHolder = ChildViewHolder(rowView)
            rowView.tag = viewHolder
        }else {
            rowView = convertView
            viewHolder = rowView.tag as ChildViewHolder
        }

        viewHolder.tvInfoName?.text = child.serviceName


        return rowView as View

    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return titleList.size
    }

    private class ChildViewHolder(view: View?) {
        val tvInfoName = view?.findViewById(R.id.tvServiceName) as TextView
        val tvInfoValue = view?.tvDescription
        val tgService = view?.findViewById(R.id.tgService) as ToggleButton
    }

    private class GroupViewHolder(view: View?) {
        val tvGroupName = view?.findViewById(R.id.tvGroupName) as TextView
        val tvCount = view?.findViewById(R.id.tvCount) as TextView
    }

}