package kz.atc.mobapp.models.main

data class BlockUnblockRequest(
    var block: String? = null,
    var codeword: String? = null,
    var reason_desc: String? = null
)