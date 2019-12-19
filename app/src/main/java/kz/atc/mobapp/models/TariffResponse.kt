package kz.atc.mobapp.models

data class TariffResponse(
    val charge_date: String,
    val constructor: Constructor,
    val options: List<Option>,
    val tariff: Tariff
)