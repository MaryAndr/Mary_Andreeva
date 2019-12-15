package kz.atc.mobapp.models

data class EmailCosts(
    var date_from: String? = null,
    var date_to: String? = null,
    var emails: MutableList<String>? = mutableListOf()
)