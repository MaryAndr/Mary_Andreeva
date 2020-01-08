package kz.atc.mobapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.tariffs_item.view.*
import kz.atc.mobapp.R
import kz.atc.mobapp.models.main.TariffShow

class AvailableTariffsAdapter(val context: Context, val items: MutableList<TariffShow>):
    RecyclerView.Adapter<AvailableTariffViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableTariffViewHolder {
        return AvailableTariffViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.tariffs_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: AvailableTariffViewHolder, position: Int) {
        holder.tvInfoName.text = items[position].name
    }


}


class AvailableTariffViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvInfoName = view.tvName
    val tvDescription = view.tvDescription
    val tvPrice = view.tvPrice
}