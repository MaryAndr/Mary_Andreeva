package kz.atc.mobapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.services_item.view.*
import kz.atc.mobapp.R
import kz.atc.mobapp.dialogs.ServiceConfirmationDialogMVI
import kz.atc.mobapp.models.main.ServiceDialogModel
import kz.atc.mobapp.models.main.ServicesListShow
import kz.atc.mobapp.models.main.ToggleButtonState
import kz.atc.mobapp.models.main.ToggleButtonState.*
import kz.atc.mobapp.utils.TimeUtils
import java.util.*

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
        holder.tvDescription.text = items[position].description
        holder.tvPriceRate.text = items[position].price + "/" + items[position].interval
        when(items[position].toggleState) {
            ActiveAndEnabled -> {
                holder.tgService.isChecked = true
                holder.tgService.isEnabled = true
                holder.tgService.setOnCheckedChangeListener { compoundButton, isChecked ->
                    if(!isChecked) {
                        val dataToPass = ServiceDialogModel()
                        dataToPass.serv_name = items[position].serviceName
                        dataToPass.serv_id = items[position].id
                        dataToPass.isConnection = false
                        dataToPass.itemHolder = holder
                        dataToPass.conDate = TimeUtils().dateToString(Calendar.getInstance())
                        val dialog = ServiceConfirmationDialogMVI.newInstance(dataToPass)
                        dialog.show(
                            (context as AppCompatActivity).supportFragmentManager,
                            "Accept Dialog"
                        )
                    }
                }
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
    val tvDescription = view.tvDescription
    val tvPriceRate = view.tvPriceRate
    val tvInfoValue = view.tvDescription
    val tgService = view.tgService
}