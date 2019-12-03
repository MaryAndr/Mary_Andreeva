package kz.atc.mobapp.states.main

import kz.atc.mobapp.models.main.MainPagaAccumData

class MainPageState(var mainDataLoaded: Boolean,
                    var mainData: MainPagaAccumData? = null,
                    var errorShown: Boolean,
                    var errrorText: String? = null)