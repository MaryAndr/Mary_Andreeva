package kz.atc.mobapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.services_item.view.*
import kz.atc.mobapp.R
import kz.atc.mobapp.models.main.ServicesListShow
import kz.atc.mobapp.models.main.ToggleButtonState
import kz.atc.mobapp.models.main.ToggleButtonState.*

class EnabledServicesAdapter(val context: Context, val items: MutableList<ServicesListShow>) :
    RecyclerView.Adapter<ServicesViewHolder>() {
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
        when(items[position].toggleState) {
            ActiveAndEnabled -> {
                holder.tgService.isChecked = true
                holder.tgService.isEnabled = true
            }
            ActiveAndDisabled -> {
                holder.tgService.isChecked = true
                holder.tgService.isEnabled = false
            }
        }
    }

}

class ServicesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvInfoName = view.tvServiceName
    val tvInfoValue = view.tvDescription
    val tgService = view.tgService
}