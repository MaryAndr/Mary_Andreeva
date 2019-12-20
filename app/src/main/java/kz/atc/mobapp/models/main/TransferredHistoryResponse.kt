package kz.atc.mobapp.models.main

data class TransferredHistoryResponse(
    val amount: Int,
    val due_date: String,
    val exchange: Double,
    val services: Services,
    val type: String,
    val unit: String
)