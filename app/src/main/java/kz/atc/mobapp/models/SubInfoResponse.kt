package kz.atc.mobapp.models

data class SubInfoResponse(
    val contract_number: String,
    val full_name: String,
    val msisdn: String,
    val personal_account: Int,
    val region: Region
)