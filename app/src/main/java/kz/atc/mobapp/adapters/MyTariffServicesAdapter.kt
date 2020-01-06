package kz.atc.mobapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.added_services_list.view.*
import kotlinx.android.synthetic.main.added_services_list.view.tvDescription
import kotlinx.android.synthetic.main.added_services_list.view.tvName
import kotlinx.android.synthetic.main.added_services_list.view.tvValue
import kotlinx.android.synthetic.main.my_tariff_services_list.view.*
import kz.atc.mobapp.R
import kz.atc.mobapp.models.ErrorJson
import kz.atc.mobapp.models.main.ServicesListShow
import kz.atc.mobapp.presenters.interactors.SubscriberInteractor
import retrofit2.HttpException

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
        holder.tvValue.text = items?.get(position)?.price + " ${context.resources.getString(R.string.rub_value)}/сутки"
        holder.tgButton.isChecked = true
        if (holder.tgButton.isEnabled) {
            holder.tgButton.setOnCheckedChangeListener { _, isChecked ->
                if(!isChecked) {
                    myCompositeDisposable?.add(
                        services.subService.deleteService(items?.get(position)?.id)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                removeItem(position)
                            }, { error ->
                                var errMessage = error.localizedMessage
                                if (error is HttpException) {
                                    errMessage = if (error.code() == 409) {
                                        "Вы не являетесь пользователем мобильной связи"
                                    } else {
                                        val errorBody = error.response()!!.errorBody()

                                        val adapter =
                                            gson.getAdapter<ErrorJson>(ErrorJson::class.java!!)
                                        val errorObj = adapter.fromJson(errorBody!!.string())
                                        errorObj.error_description
                                    }
                                }
                                holder.tgButton.isChecked = true
                                holder.tgButton.isEnabled = true
                                Toast.makeText(context, errMessage, Toast.LENGTH_LONG).show()

                            })
                    )
                }
            }
        }
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