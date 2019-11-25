package kz.atc.mobapp.models

data class OAuthRequestModel(val grant_type: String, val password: String?, val username : String?, val refresh_token : String?)