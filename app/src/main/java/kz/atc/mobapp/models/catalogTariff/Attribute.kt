package kz.atc.mobapp.models.catalogTariff

data class Attribute(
    val id: Int,
    val name: String,
    val notice: String,
    val `param`: String,
    val system_name: String,
    val unit: String,
    val value: String
)