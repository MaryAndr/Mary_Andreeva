package kz.atc.mobapp.models.catalogTariff

data class Service(
    val attributes: List<AttributeX>,
    val id: Int,
    val name: String,
    val region: String
)