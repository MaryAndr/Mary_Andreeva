package ru.filit.motiv.app.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.services_item.view.*

class ServicesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvInfoName = view.tvServiceName
    val tvDescription = view.tvDescription
    val tvPriceRate = view.tvPriceRate
    val tgService = view.tgService
}