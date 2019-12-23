package kz.atc.mobapp.models.main

data class IndicatorHolder(
    var rest: Int? = null,
    var total: Int? = null,
    var percent: Int? = null,
    var unlim: Boolean,
    var valueUnit: String? = null,
    var optionsName: String? = null,
    var dueDate: String? = null
)
