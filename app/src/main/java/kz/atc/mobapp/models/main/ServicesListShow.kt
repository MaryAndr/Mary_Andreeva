package kz.atc.mobapp.models.main

data class ServicesListShow(
    var id: String? = null,
    var serviceName: String? = null,
    var description: String? = null,
    var price: String? = null,
    var rate: String? = null,
    var toggleState: ToggleButtonState? = null
)

enum class ToggleButtonState{
    ActiveAndEnabled, ActiveAndDisabled, InaactiveAndEnabled, InactiveAndDisabled
}