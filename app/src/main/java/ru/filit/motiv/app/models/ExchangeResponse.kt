package ru.filit.motiv.app.models

data class ExchangeResponse(
    val available: Boolean,
    val max_minutes: Int,
    val rate: Double
)