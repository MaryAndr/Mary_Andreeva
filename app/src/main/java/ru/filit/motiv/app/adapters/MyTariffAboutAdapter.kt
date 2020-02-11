package ru.filit.motiv.app.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.my_tariff_services_list.view.*
import ru.filit.motiv.app.R
import ru.filit.motiv.app.models.catalogTariff.Attribute

class MyTariffAboutAdapter(attribute: MutableList<Attribute>, val context: Context) :
    RecyclerView.Adapter<AboutViewHolder>() {

    private val allowedServices = mutableListOf(
        "Включено в абонентскую плату",
        "Услуги, доступные на тарифе",
        "Сверх абонентской платы",
        "Услуги сверх пакета или при несписании абонентской платы",
        "При списании абонентской платы (сверх пакета)",
        "При несписаной абонентской плате"
    )

    private val keywords = mutableListOf(
        "Исходящие звонки",
        "Мобильный интернет",
        "Исходящие SMS-сообщения",
        "Исходящие MMS-сообщения",
        "Входящие"
    )

    private val attributes =
        attribute.filter { pred -> pred.name in allowedServices}
            .sortedWith ( compareBy({it.name},{it.param}) )


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

        if (previousItem == null || attributes?.get(position)?.name != previousItem?.name) {
            holder.headerLayout.visibility = View.VISIBLE
            holder.tvHeader.text = attributes?.get(position)?.name
        }


//        if (attributes[position]?.name.length > 30) {
//            holder.tvName.textSize = TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_PX,
//                11f,
//                context.resources.displayMetrics
//            )
//        }
        val match = keywords.filter { it in attributes[position]?.param}
        if (match.isNotEmpty()) {
            holder.tvName.text = match.first()
            holder.tvDescription.text =
                attributes[position].param.substring(attributes[position].param.indexOf(match.first()) + match.first().length)
                    .trim()
        } else {
            holder.tvDescription.visibility = View.GONE
            holder.tvName.text = attributes[position]?.param
        }
//        holder.tvName.text = attributes[position]?.name
//        holder.tvDescription.text = attributes[position]?.notice
//        if (attributes[position]?.value?.length > 20 || attributes[position]?.unit?.length > 20) {
//            holder.tvValue.textSize = TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_PX,
//                11f,
//                context.resources.displayMetrics
//            )
//        }
        val value =
            attributes[position]?.value.orEmpty() + " " + attributes[position]?.unit.orEmpty()

        holder.tvValue.text = value.trim()

        if (value.contains("Приложение №5")){
            holder.tvValue.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            holder.tvValue.setOnClickListener{
                openPdf("http://cell.motivtelecom.ru/uploads/files/files/pril_5.pdf")
            }
        }
        if (value.contains("Приложение №7")){
            holder.tvValue.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            holder.tvValue.setOnClickListener{
                openPdf("http://cell.motivtelecom.ru/uploads/files/files/Prilozhenie_7_010818.pdf")
            }
        }

    }

    private fun openPdf(url:String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }


}

class AboutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvName = view.tvName
    val tvDescription = view.tvDescription
    val tvValue = view.tvValue
    val tvHeader = view.tvHeader
    val headerLayout = view.headerLayout
}