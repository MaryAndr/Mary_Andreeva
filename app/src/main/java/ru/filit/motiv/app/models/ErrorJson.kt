package ru.filit.motiv.app.models

data class ErrorJson(
    val error_code: String,
    val error_description: String,
    val additional_info: AdditionalInfo
)