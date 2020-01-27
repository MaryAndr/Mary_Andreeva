package ru.filit.motiv.app.models

data class TariffResponse(
    val charge_date: String,
    val options: List<Option>,
    val tariff: Tariff
)