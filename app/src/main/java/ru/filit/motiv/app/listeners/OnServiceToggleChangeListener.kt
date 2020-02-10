package ru.filit.motiv.app.listeners

import ru.filit.motiv.app.models.main.ServicesListShow

interface OnServiceToggleChangeListener {
    fun onToggleClick(item: ServicesListShow, isChecked: Boolean, position: Int) {}
}
