package ru.filit.motiv.app.models.main

data class SubscriberInfoResponse(
    val msisdn: String,
    val contract_date: String,
    val contract_number: String,
    val full_name: String,
    val personal_account: Int,
    val region: Region
)