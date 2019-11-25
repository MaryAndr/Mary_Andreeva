package kz.atc.mobapp.oauth

import kz.atc.mobapp.presenters.interactors.UserInteractor
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        return null
    }
}