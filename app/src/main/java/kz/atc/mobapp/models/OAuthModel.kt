package kz.atc.mobapp.models

data class OAuthModel(
    val access_token: String,
    val expires_in: Int,
    val jti: String,
    val refresh_token: String,
    val scope: String,
    val token_type: String
)