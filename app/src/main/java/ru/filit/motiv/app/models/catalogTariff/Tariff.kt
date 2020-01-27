package ru.filit.motiv.app.models.catalogTariff

data class Tariff(
    val attributes: MutableList<Attribute>,
    val id: Int,
    val name: String
)