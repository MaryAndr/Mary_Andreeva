package ru.filit.motiv.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.added_services_list.view.*
import kotlinx.android.synthetic.main.added_services_list.view.tvDescription
import kotlinx.android.synthetic.main.added_services_list.view.tvName
import kotlinx.android.synthetic.main.added_services_list.view.tvValue
import ru.filit.motiv.app.R
import ru.filit.motiv.app.models.ErrorJson
import ru.filit.motiv.app.models.main.ServicesListShow
import ru.filit.motiv.app.presenters.interactors.SubscriberInteractor
import retrofit2.HttpException
import ru.filit.motiv.app.dialogs.ServiceConfirmationDialogMVI
import ru.filit.motiv.app.models.main.ServiceDialogModel
import ru.filit.motiv.app.models.main.ToggleButtonState
import ru.filit.motiv.app.utils.TimeUtils
import java.util.*

class MyTariffServicesAdapter(val items: MutableList<ServicesListShow>?, val context: Context) :
    RecyclerView.Adapter<ViewHolder>() {
    private val services = SubscriberInteractor(context)
    private val gson = Gson()
    private var myCompositeDisposable: CompositeDisposable? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        myCompositeDisposable = CompositeDisposable()
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

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        myCompositeDisposable?.clear()

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = items?.get(position)?.serviceName
        holder.tvDescription.text = items?.get(position)?.description
        holder.tvValue.text = items?.get(position)?.price + " ${context.resources.getString(R.string.rub_value)}/месяц"
        when(items?.get(position)?.toggleState) {
            ToggleButtonState.ActiveAndEnabled -> {
                holder.tgButton.isChecked = true
                holder.tgButton.isEnabled = true
                holder.tgButton.setOnCheckedChangeListener { compoundButton, isChecked ->
                    if (!isChecked) {
                        val dataToPass = ServiceDialogModel()
                        dataToPass.serv_name = items!![position].serviceName
                        dataToPass.serv_id = items!![position].id
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
            ToggleButtonState.ActiveAndDisabled -> {
                holder.tgButton.isChecked = true
                holder.tgButton.isEnabled = false
                holder.tgButton.setBackgroundResource(R.drawable.toggle_dis_on)
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

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvName = view.tvName
    val tvDescription = view.tvDescription
    val tvValue = view.tvValue
    val tgButton = view.tgButton
}