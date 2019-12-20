package kz.atc.mobapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.added_services_list.view.*
import kz.atc.mobapp.R
import kz.atc.mobapp.models.main.ServicesListShow

class MyTariffServicesAdapter (val items : MutableList<ServicesListShow>?, val context: Context)
    : RecyclerView.Adapter<ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.added_services_list, parent, false))
    }


    override fun getItemCount(): Int {
        return items!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = items?.get(position)?.serviceName
        holder.tvDescription.text = items?.get(position)?.description
        holder.tvValue.text = items?.get(position)?.price
    }

}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val tvName = view.tvName
    val tvDescription = view.tvDescription
    val tvValue = view.tvValue
}