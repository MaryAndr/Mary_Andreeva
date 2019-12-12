package kz.atc.mobapp.models.catalogTariff

data class Tariff(
    val attributes: List<Attribute>,
    val id: Int,
    val name: String
)