package kz.atc.mobapp.models.main

data class ChangePassRequest(
    val new_password: String,
    val old_password: String
)