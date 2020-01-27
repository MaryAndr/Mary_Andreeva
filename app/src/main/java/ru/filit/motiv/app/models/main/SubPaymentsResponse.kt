package ru.filit.motiv.app.models.main

data class SubPaymentsResponse(
    val amount: Double,
    val date: String,
    val gateway: String
)