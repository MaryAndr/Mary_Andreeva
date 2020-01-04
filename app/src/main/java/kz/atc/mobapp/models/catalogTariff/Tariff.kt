package kz.atc.mobapp.models.catalogTariff

data class Tariff(
    val attributes: MutableList<Attribute>,
    val id: Int,
    val name: String
)