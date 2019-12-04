package kz.atc.mobapp.models

data class RemainsResponse(
    val due_date: String,
    val rest_amount: Int,
    val services: Services,
    val total_amount: Int,
    val type: String,
    val unit: String
)