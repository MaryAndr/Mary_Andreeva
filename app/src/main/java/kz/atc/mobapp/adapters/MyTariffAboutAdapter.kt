package kz.atc.mobapp.adapters

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.my_tariff_services_list.view.*
import kz.atc.mobapp.R
import kz.atc.mobapp.models.catalogTariff.Attribute
import kz.atc.mobapp.models.catalogTariff.CatalogTariffResponse
import kz.atc.mobapp.models.main.ServicesListShow

class MyTariffAboutAdapter(val catalogTariff: CatalogTariffResponse, val context: Context) :
    RecyclerView.Adapter<AboutViewHolder>() {

    private val allowedServices = mutableListOf(
        "Включено в абонентскую плату",
        "Услуги, доступные на тарифе",
        "Сверх абонентской платы",
        "Услуги сверх пакета или при несписании абонентской платы",
        "При списании абонентской платы (сверх пакета)",
        "При несписаной абонентской плате"
    )

    private val attributes = catalogTariff.tariffs.first().attributes.filter { pred -> pred.name in allowedServices }.sortedBy { it.name }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutViewHolder {
        return AboutViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.my_tariff_services_list,
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return attributes.size
    }

    override fun onBindViewHolder(holder: AboutViewHolder, position: Int) {


        var previousItem: Attribute? = null

        if (position != 0) {
            previousItem = attributes?.get(position - 1)
        }

        if (previousItem == null && attributes?.get(position)?.name != previousItem?.name) {
            holder.headerLayout.visibility = View.VISIBLE
            holder.tvHeader.text = attributes?.get(position)?.name
        }


        if (attributes[position]?.name.length > 30) {
            holder.tvName.textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                11f,
                context.resources.displayMetrics
            )
        }
        holder.tvName.text = attributes[position]?.name
        holder.tvDescription.text = attributes[position]?.param
//        if (attributes[position]?.value?.length > 20 || attributes[position]?.unit?.length > 20) {
//            holder.tvValue.textSize = TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_PX,
//                11f,
//                context.resources.displayMetrics
//            )
//        }

        holder.tvValue.text =
            attributes[position]?.value.orEmpty() + attributes[position]?.unit.orEmpty()
    }


}

class AboutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvName = view.tvName
    val tvDescription = view.tvDescription
    val tvValue = view.tvValue
    val tvHeader = view.tvHeader
    val headerLayout = view.headerLayout
}