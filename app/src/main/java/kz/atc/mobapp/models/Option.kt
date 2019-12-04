package kz.atc.mobapp.models

data class Option(
    val id: Int,
    val name: String,
    val notice: String,
    val option: String,
    val primary: Boolean,
    val type: String
)