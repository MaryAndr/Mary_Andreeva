package kz.atc.mobapp.states.main

import kz.atc.mobapp.models.main.MyTariffMainData

class MyTariffState (var mainDataLoaded: Boolean,
                     var mainData: MyTariffMainData? = null,
                     var errorShown: Boolean,
                     var errorText: String? = null
                     )