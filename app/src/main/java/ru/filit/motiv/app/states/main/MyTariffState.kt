package ru.filit.motiv.app.states.main

import ru.filit.motiv.app.models.main.MyTariffMainData

class MyTariffState (var mainDataLoaded: Boolean,
                     var mainData: MyTariffMainData? = null,
                     var errorShown: Boolean,
                     var errorText: String? = null,
                     var loading: Boolean,
                     var changeService: Boolean,
                     var changeServiceMessage: String?
                     )