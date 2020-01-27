package ru.filit.motiv.app.models.main

data class BlockUnblockDataModel(
    val isBlock: Boolean,
    val codeword: String?,
    val reason: String?
)