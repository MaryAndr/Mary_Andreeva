package kz.atc.mobapp.models.main

data class ServicesListResponse(
    val id: Int,
    val interval: Interval,
    val name: String,
    val price: Double,
    val status: Status,
    val unlock: Boolean
)