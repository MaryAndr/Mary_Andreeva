package kz.atc.mobapp.models.main

data class BlockUnblockDataModel(
    val isBlock: Boolean,
    val codeword: String?,
    val reason: String?
)