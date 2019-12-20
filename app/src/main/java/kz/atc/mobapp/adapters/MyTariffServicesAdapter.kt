package kz.atc.mobapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.view.enabled
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.added_services_list.view.*
import kz.atc.mobapp.R
import kz.atc.mobapp.models.main.ServicesListShow
import kz.atc.mobapp.presenters.interactors.SubscriberInteractor

class MyTariffServicesAdapter(val items: MutableList<ServicesListShow>?, val context: Context) :
    RecyclerView.Adapter<ViewHolder>() {
    private val services = SubscriberInteractor(context)

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
        holder.tvValue.text = items?.get(position)?.price
        holder.tgButton.isChecked = true
        if (holder.tgButton.isEnabled) {

            holder.tgButton.setOnCheckedChangeListener { view, isChecked ->
                myCompositeDisposable?.add(services.subService.deleteService(items?.get(position)?.id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        removeItem(position)
                    }, { error ->
                        holder.tgButton.isEnabled = true
                        error.printStackTrace()
                    })
                )

            }
        }
    }

    fun removeItem(position: Int) {
        items?.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items?.size!!)
    }

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvName = view.tvName
    val tvDescription = view.tvDescription
    val tvValue = view.tvValue
    val tgButton = view.tgButton
}