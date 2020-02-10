package ru.filit.motiv.app.models.main

data class ServicesListResponse(
    val id: Int,
    val interval: Interval?,
    val name: String,
    val price: Double,
    val status: Status,
    val unlock: Boolean,
    val price_on: Double
)