package ru.filit.motiv.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import io.reactivex.subjects.BehaviorSubject
import ru.filit.motiv.app.R
import ru.filit.motiv.app.dialogs.MyTariffAboutDialog
import ru.filit.motiv.app.models.main.MyTariffAboutData
import ru.filit.motiv.app.models.main.TariffShow

class ExpandableTariffsCategories internal constructor(
    private val context: Context,
    private val titleList: List<String?>,
    private val dataList: MutableMap<String, MutableList<TariffShow>>,
    private val reloadTrigger: BehaviorSubject<Int>? = null
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
            val layoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowView = layoutInflater.inflate(R.layout.services_group_item, null)
            viewHolder = GroupViewHolder(rowView)
            rowView.tag = viewHolder
        } else {
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
            val layoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowView = layoutInflater.inflate(R.layout.tariffs_item, null)
            viewHolder = ChildTariffViewHolder(rowView)
            rowView.tag = viewHolder
        } else {
            rowView = convertView
            viewHolder = rowView.tag as ChildTariffViewHolder
        }


        if (child.isNew) {
            viewHolder.ivNew.visibility = View.VISIBLE
        }

        viewHolder.tvInfoName.text = child.name

        if (child.dataValueUnit != null) {
            viewHolder.addDataView.visibility = View.VISIBLE
            viewHolder.tvAddData.text = child.dataValueUnit
        } else {
            viewHolder.addDataView.visibility = View.INVISIBLE
        }

        if (child.voiceValueUnit != null) {
            viewHolder.addVoiceView.visibility = View.VISIBLE
            viewHolder.tvAddVoice.text = child.voiceValueUnit
        } else {
            viewHolder.addVoiceView.visibility = View.INVISIBLE
        }

        if (child.smsValueUnit != null) {
            viewHolder.addSmsView.visibility = View.VISIBLE
            viewHolder.tvAddSms.text = child.smsValueUnit
        } else {
            viewHolder.addSmsView.visibility = View.INVISIBLE
        }

        if (child.price != null) {
            viewHolder.tvPrice.text =
                "${child.price} ${context.resources.getString(R.string.rub_value)}/месяц"
        } else {
            viewHolder.tvPrice.visibility = View.INVISIBLE
        }

        if (child.description != null) {
            viewHolder.tvDescription.text = child.description
        } else {
            viewHolder.tvDescription.visibility = View.INVISIBLE
        }

        if (child.aboutData != null) {
            val activity = context as FragmentActivity
            viewHolder.tvDetails.setOnClickListener {
                val aboutDialog = MyTariffAboutDialog.newInstance(MyTariffAboutData( catalogTariff = child.aboutData!!.catalogTariff, subscriberServices = child.aboutData!!.subscriberServices), true, reloadTrigger)
                aboutDialog.show(
                    activity!!.supportFragmentManager,
                    "my_tariff_dialog_fragment"
                )
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

    private class ChildTariffViewHolder(view: View?) {
        val tvInfoName = view?.findViewById(R.id.tvName) as TextView
        val addDataView = view?.findViewById(R.id.addDataView) as LinearLayout
        val tvAddData = view?.findViewById(R.id.tvAddData) as TextView
        val addVoiceView = view?.findViewById(R.id.addVoiceView) as LinearLayout
        val tvAddVoice = view?.findViewById(R.id.tvAddVoice) as TextView
        val addSmsView = view?.findViewById(R.id.addSMSView) as LinearLayout
        val tvAddSms = view?.findViewById(R.id.tvAddSMS) as TextView
        val tvPrice = view?.findViewById(R.id.tvPrice) as TextView
        val tvDescription = view?.findViewById(R.id.tvDescription) as TextView
        val tvDetails = view?.findViewById(R.id.tvDetails) as TextView
        val ivNew = view?.findViewById(R.id.ivNew) as ImageView
    }

    private class GroupViewHolder(view: View?) {
        val tvGroupName = view?.findViewById(R.id.tvGroupName) as TextView
        val tvCount = view?.findViewById(R.id.tvCount) as TextView
        val imgIndicator = view?.findViewById(R.id.expandImg) as ImageView
    }
}