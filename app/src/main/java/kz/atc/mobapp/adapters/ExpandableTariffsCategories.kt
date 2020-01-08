package kz.atc.mobapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import kz.atc.mobapp.R
import kz.atc.mobapp.models.main.TariffShow
import java.util.*

class ExpandableTariffsCategories internal constructor(
    private val context: Context,
    private val titleList: List<String?>,
    private val dataList: MutableMap<String, MutableList<TariffShow>>
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
        val viewHolder: ExpandableTariffsCategories.GroupViewHolder
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

    override fun getChild(groupPosition: Int, childPosition: Int): TariffShow {
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
        val viewHolder: ExpandableTariffsCategories.ChildTariffViewHolder
        val rowView: View?
        val child = getChild(groupPosition, childPosition)

        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowView = layoutInflater.inflate(R.layout.tariffs_item, null)
            viewHolder = ChildTariffViewHolder(rowView)
            rowView.tag = viewHolder
        }else {
            rowView = convertView
            viewHolder = rowView.tag as ChildTariffViewHolder
        }

        viewHolder.tvInfoName.text = getChild(groupPosition,childPosition).name
        return rowView as View
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return titleList.size
    }

    private class ChildTariffViewHolder(view: View?) {
        val tvInfoName = view?.findViewById(R.id.tvName) as TextView
    }

    private class GroupViewHolder(view: View?) {
        val tvGroupName = view?.findViewById(R.id.tvGroupName) as TextView
        val tvCount = view?.findViewById(R.id.tvCount) as TextView
        val imgIndicator = view?.findViewById(R.id.expandImg) as ImageView
    }
}