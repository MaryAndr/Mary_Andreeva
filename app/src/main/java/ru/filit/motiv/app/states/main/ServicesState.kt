package ru.filit.motiv.app.states.main

import ru.filit.motiv.app.models.main.ServicesListShow

sealed class ServicesState {

    data class FetchEnabledService(val servicesList: MutableList<ServicesListShow>) : ServicesState()

    data class FetchAllService(val servicesList: MutableList<ServicesListShow>) : ServicesState()

    object Loading: ServicesState()
}