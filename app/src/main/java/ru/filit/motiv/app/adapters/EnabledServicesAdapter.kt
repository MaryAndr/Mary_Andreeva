package ru.filit.motiv.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.filit.motiv.app.R
import ru.filit.motiv.app.listeners.OnServiceToggleChangeListner
import ru.filit.motiv.app.models.main.ServicesListShow
import ru.filit.motiv.app.models.main.ToggleButtonState.*

class EnabledServicesAdapter(val context: Context
                             ,var onToggleChangeListener: OnServiceToggleChangeListner) : RecyclerView.Adapter<ServicesViewHolder>() {

    private var items: MutableList<ServicesListShow> = mutableListOf()

    fun setData (items: MutableList<ServicesListShow>){
        this.items = items
    }

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
        holder.tvDescription.text = items[position].description
        holder.tvPriceRate.text = items[position].price + " Руб/" + items[position].interval
        holder.tgService.isChecked = true
        when(items[position].toggleState) {
            ActiveAndEnabled -> {
                    holder.tgService.isEnabled = true
                    holder.tgService.setOnCheckedChangeListener { compoundButton, isChecked ->
                        onToggleChangeListener.onToggleClick(items[position], isChecked)
                }
            }
            ActiveAndDisabled -> {
                holder.tgService.isChecked = true
                holder.tgService.isEnabled = false
                holder.tgService.setBackgroundResource(R.drawable.toggle_dis_on)
            }
            InactiveAndEnabled -> {
                holder.tgService.setOnClickListener(null)
            }
        }
    }
}

