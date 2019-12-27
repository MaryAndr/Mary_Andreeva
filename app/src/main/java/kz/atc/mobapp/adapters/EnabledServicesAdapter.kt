package kz.atc.mobapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.services_item.view.*
import kz.atc.mobapp.R
import kz.atc.mobapp.models.main.ServicesListShow

class EnabledServicesAdapter (val context: Context, val items: MutableList<ServicesListShow>) : RecyclerView.Adapter<ServicesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicesViewHolder {
        return ServicesViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.services_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ServicesViewHolder, position: Int) {
        holder.tvInfoName.text = items[position].serviceName
    }

}

class ServicesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvInfoName = view.tvServiceName
    val tvInfoValue = view.tvDescription
}