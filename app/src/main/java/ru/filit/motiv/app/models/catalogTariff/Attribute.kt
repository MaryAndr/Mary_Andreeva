package ru.filit.motiv.app.models.catalogTariff

data class Attribute(
    val id: Int,
    val name: String,
    val notice: String,
    val `param`: String,
    val system_name: String,
    val unit: String,
    val value: String
)