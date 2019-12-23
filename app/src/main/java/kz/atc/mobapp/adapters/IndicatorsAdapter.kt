package kz.atc.mobapp.adapters

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.indictors_list.view.*
import kz.atc.mobapp.R
import kz.atc.mobapp.models.main.IndicatorHolder
import kz.atc.mobapp.models.main.IndicatorsModel

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
            holder.tvConditionRest.text = indicatorsModels[position].rest.toString()
            holder.tvconditionTotal.text = indicatorsModels[position].total.toString()
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