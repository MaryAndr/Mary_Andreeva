package kz.atc.mobapp.states.main

import kz.atc.mobapp.models.main.ServicesListShow

sealed class ServicesState {

    data class FetchEnabledService(val servicesList: MutableList<ServicesListShow>) : ServicesState()

    data class FetchAllService(val obj: Unit) : ServicesState()
}