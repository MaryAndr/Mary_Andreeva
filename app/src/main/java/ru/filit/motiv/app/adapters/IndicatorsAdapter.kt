package ru.filit.motiv.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.indictors_list.view.*
import ru.filit.motiv.app.R
import ru.filit.motiv.app.models.main.IndicatorHolder
import ru.filit.motiv.app.utils.StringUtils

class IndicatorsAdapter(val indicatorsModels: MutableList<IndicatorHolder>, val context: Context) :
    RecyclerView.Adapter<IndicatorViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndicatorViewHolder {
        return IndicatorViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.indictors_list,
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return indicatorsModels.size
    }

    override fun onBindViewHolder(holder: IndicatorViewHolder, position: Int) {
        holder.tvConditionName.text = indicatorsModels[position].optionsName
        if (indicatorsModels[position].unlim) {
            holder.tvConditionRest.text = "Безлимит"
            holder.tvconditionTotal.text = ""
            holder.pbCondition.progress = 100
        } else {
            if (indicatorsModels[position].type == "DATA") {
                holder.tvConditionRest.text =
                    "${StringUtils().unitValueConverter(indicatorsModels[position].rest!!).value} ${StringUtils().unitValueConverter(
                        indicatorsModels[position].rest!!
                    ).unit}"
                holder.tvconditionTotal.text =
                    "из ${StringUtils().unitValueConverter(indicatorsModels[position].total!!).value} ${StringUtils().unitValueConverter(
                        indicatorsModels[position].total!!
                    ).unit}"
            } else {
                    if (indicatorsModels[position].type == "VOICE") {
                        holder.tvConditionRest.text =
                            indicatorsModels[position].rest.toString() + " Мин"
                        holder.tvconditionTotal.text =
                            "из ${indicatorsModels[position].total.toString()} Мин"
                    } else if (indicatorsModels[position].type == "SMS") {
                        holder.tvConditionRest.text =
                            indicatorsModels[position].rest.toString() + " SMS"
                        holder.tvconditionTotal.text =
                            "из ${indicatorsModels[position].total.toString()} SMS"
                    }
            }
            holder.pbCondition.progress = indicatorsModels[position].percent!!
        }

        if (indicatorsModels[position].dueDate.isNullOrEmpty()) {
            holder.tvConditionData.text = indicatorsModels[position].dueDate
        } else {
            holder.tvConditionData.visibility = View.GONE
        }


    }


}

class IndicatorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvConditionName = view.tvConditionsName
    val tvConditionRest = view.tvConditionsRest
    val pbCondition = view.pbConditions
    val tvConditionData = view.tvConditionsDate
    val tvconditionTotal = view.tvConditionsTotal
}