package ru.filit.motiv.app.states.main

import ru.filit.motiv.app.models.main.ServicesListShow

data class ServicesState(var services: MutableList<ServicesListShow>? = null,
                         var loadedenabledServices: Boolean,
                         var loadedAllServices: Boolean,
                         var loading:Boolean)