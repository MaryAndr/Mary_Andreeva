package ru.filit.motiv.app.listeners

import ru.filit.motiv.app.models.main.ServicesListShow

interface OnServiceToggleChangeListner {
    fun onToggleClick(item: ServicesListShow, isChecked: Boolean) {}
}
