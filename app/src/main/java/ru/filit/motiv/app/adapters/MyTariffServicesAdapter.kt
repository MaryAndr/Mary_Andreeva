package ru.filit.motiv.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.added_services_list.view.*
import kotlinx.android.synthetic.main.added_services_list.view.tvDescription
import kotlinx.android.synthetic.main.added_services_list.view.tvName
import kotlinx.android.synthetic.main.added_services_list.view.tvValue
import ru.filit.motiv.app.R
import ru.filit.motiv.app.models.main.ServicesListShow
import ru.filit.motiv.app.presenters.interactors.SubscriberInteractor
import ru.filit.motiv.app.listeners.OnServiceToggleChangeListener
import ru.filit.motiv.app.models.main.ToggleButtonState

class MyTariffServicesAdapter(val items: MutableList<ServicesListShow>?, val context: Context, val onServiceToggleChangeListner: OnServiceToggleChangeListener) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.added_services_list,
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return items!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = items?.get(position)?.serviceName
        holder.tvDescription.text = items?.get(position)?.description
        holder.tvValue.text = items?.get(position)?.price?.substringBefore(".0") + " ${context.resources.getString(R.string.rub_value)}/" + items?.get(position)?.interval
        holder.tgButton.setOnCheckedChangeListener(null)
        when(items?.get(position)?.toggleState) {
            ToggleButtonState.ActiveAndEnabled -> {
                holder.tgButton.isChecked = true
                holder.tgButton.isEnabled = true
                holder.tgButton.setOnCheckedChangeListener { compoundButton, isChecked ->
                    onServiceToggleChangeListner.onToggleClick(items[position], isChecked,position)
                }
            }
            ToggleButtonState.ActiveAndDisabled -> {
                holder.tgButton.isChecked = true
                holder.tgButton.isEnabled = false
            }
        }

//        if (holder.tgButton.isEnabled) {
//            holder.tgButton.setOnCheckedChangeListener { _, isChecked ->
//                if (!isChecked) {
//                    myCompositeDisposable?.add(
//                        services.subService.deleteService(items?.get(position)?.id)
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribeOn(Schedulers.io())
//                            .subscribe({
//                                removeItem(position)
//                            }, { error ->
//                                var errMessage = error.localizedMessage
//                                if (error is HttpException) {
//                                    errMessage = if (error.code() == 409) {
//                                        "Вы не являетесь пользователем мобильной связи"
//                                    } else {
//                                        val errorBody = error.response()!!.errorBody()
//                                        val adapter =
//                                            gson.getAdapter<ErrorJson>(ErrorJson::class.java!!)
//                                        val errorObj = adapter.fromJson(errorBody!!.string())
//                                        errorObj.error_description
//                                    }
//                                }
//                                holder.tgButton.isChecked = true
//                                holder.tgButton.isEnabled = true
//                                Toast.makeText(context, errMessage, Toast.LENGTH_LONG).show()
//                            })
//                    )
//                }
//            }
//        }
    }

    private fun removeItem(position: Int) {
        Toast.makeText(context, "Услуга успешно отключена.", Toast.LENGTH_LONG).show()

        notifyItemRangeChanged(position, items?.size!!)
        items?.removeAt(position)
        notifyItemRemoved(position)
    }

 private fun getPrice(price:String?):String?{
     val cost = price?.substringBefore(",0")
         return cost
 }
}


class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvName = view.tvName
    val tvDescription = view.tvDescription
    val tvValue = view.tvValue
    val tgButton = view.tgButton
}