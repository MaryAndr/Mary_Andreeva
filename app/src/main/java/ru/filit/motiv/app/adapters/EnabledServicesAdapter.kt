package ru.filit.motiv.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.filit.motiv.app.R
import ru.filit.motiv.app.listeners.OnServiceToggleChangeListener
import ru.filit.motiv.app.models.main.ServicesListShow
import ru.filit.motiv.app.models.main.ToggleButtonState.*

class EnabledServicesAdapter(private var onToggleChangeListener: OnServiceToggleChangeListener) : RecyclerView.Adapter<ServicesViewHolder>() {

    private var items: MutableList<ServicesListShow> = mutableListOf()

    fun setData (items: MutableList<ServicesListShow>){
        this.items = items
        notifyDataSetChanged()
    }
    fun cancelChanges ( position: Int){
        notifyItemChanged(position)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicesViewHolder {
        return ServicesViewHolder(
            LayoutInflater.from(parent.context).inflate(
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
        holder.tvPriceRate.text = items[position].price?.substringBefore(".0") + " Руб/" + items[position].interval
        holder.tgService.setOnCheckedChangeListener(null)
        when(items[position].toggleState) {
            ActiveAndEnabled -> {
                    holder.tgService.isChecked = true
                    holder.tgService.isEnabled = true
                    holder.tgService.setOnCheckedChangeListener { compoundButton, isChecked ->
                        onToggleChangeListener.onToggleClick(items[position], isChecked, position)
                    }
            }
            ActiveAndDisabled -> {
                holder.tgService.isChecked = true
                holder.tgService.isEnabled = false
            }
            else -> {holder.itemView.visibility = View.GONE}
        }
    }
}

