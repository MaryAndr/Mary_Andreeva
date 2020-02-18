package ru.filit.motiv.app.states.main

import ru.filit.motiv.app.models.main.ServiceDialogModel
import ru.filit.motiv.app.models.main.ServicesListShow

sealed class ServicesPartialState {

    data class FetchEnabledService(val servicesList: MutableList<ServicesListShow>) : ServicesPartialState()

    data class FetchAllService(val servicesList: MutableList<ServicesListShow>) : ServicesPartialState()

    data class ChangeAvailableService(val dialogMessage: String): ServicesPartialState()

    data class CancelChange(val isCancel: Boolean): ServicesPartialState()

    object Loading: ServicesPartialState()

    object LoadingChangeService : ServicesPartialState()

    data class InternetState(val active: Boolean): ServicesPartialState()
}