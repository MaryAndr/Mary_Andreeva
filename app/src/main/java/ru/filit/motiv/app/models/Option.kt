package ru.filit.motiv.app.models

data class Option(
    val id: Int,
    val name: String,
    val option: String,
    val type: String,
    val primary: Boolean
)