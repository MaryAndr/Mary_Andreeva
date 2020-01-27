package ru.filit.motiv.app.models.main

data class ChangePassRequest(
    val new_password: String,
    val old_password: String
)