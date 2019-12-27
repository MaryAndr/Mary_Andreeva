package kz.atc.mobapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.info_item.view.*
import kz.atc.mobapp.R
import kz.atc.mobapp.models.catalogTariff.Attribute

class InfoAdapter(val context: Context, val items: List<Attribute>) : RecyclerView.Adapter<InfoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        return InfoViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.info_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        holder.tvInfoName.text = items[position].param
        holder.tvInfoValue.text = items[position].value + " " + items[position].unit.orEmpty()
    }
}

class InfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvInfoName = view.tvInfoName
    val tvInfoValue = view.tvInfoValue
}