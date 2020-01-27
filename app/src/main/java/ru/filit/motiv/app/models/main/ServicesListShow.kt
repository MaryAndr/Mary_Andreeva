package ru.filit.motiv.app.models.main

data class ServicesListShow(
    var id: String? = null,
    var serviceName: String? = null,
    var description: String? = null,
    var subFee: String? = null,
    var activPrice: String? = null,
    var price: String? = null,
    var interval: String? = null,
    var rate: String? = null,
    var isExistOnSub: Boolean = false,
    var toggleState: ToggleButtonState? = ToggleButtonState.ActiveAndEnabled,
    var category: String? = null
)

enum class ToggleButtonState(state: Int){
    ActiveAndEnabled(0), ActiveAndDisabled(1), InactiveAndEnabled(2), NotShown(3)
}