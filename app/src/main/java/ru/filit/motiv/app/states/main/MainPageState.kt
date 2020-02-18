package ru.filit.motiv.app.states.main

import ru.filit.motiv.app.models.main.MainPagaAccumData

class MainPageState(var mainDataLoaded: Boolean,
                    var mainData: MainPagaAccumData? = null,
                    var errorShown: Boolean,
                    var errorText: String? = null,
                    var loading: Boolean,
                    var connectionLost: Boolean,
                    var connectionResume: Boolean
                    )