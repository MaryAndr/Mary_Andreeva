package kz.atc.mobapp.oauth

import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.atc.mobapp.presenters.interactors.UserInteractor
import kz.atc.mobapp.utils.Constants
import kz.atc.mobapp.utils.PreferenceHelper
import kz.atc.mobapp.utils.PreferenceHelper.set
import kz.atc.mobapp.utils.PreferenceHelper.get
import okhttp3.*

class TokenAuthenticator(val ctx: Context) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val prefs = PreferenceHelper.customPrefs(ctx, Constants.AUTH_PREF_NAME)
        val token: String? = prefs[Constants.AUTH_TOKEN] ?: return null

        synchronized(this) {
            val refreshToken: String? = prefs[Constants.AUTH_REFRESH_TOKEN]
            val newToken: String? = prefs[Constants.AUTH_TOKEN]
            if (newToken != token) {
                return newRequestWithAccessToken(response.request(), newToken!!)
            }

            val refreshedToken = UserInteractor().userService.auth(
                RequestBody.create(
                    MediaType.parse("text/plain"),
                    "refresh_token"
                ), null, null, RequestBody.create(MediaType.parse("text/plain"), refreshToken)
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .blockingFirst()
            prefs[Constants.AUTH_TOKEN] = refreshedToken.access_token
            prefs[Constants.AUTH_REFRESH_TOKEN] = refreshedToken.refresh_token

            return newRequestWithAccessToken(response.request(), refreshedToken.access_token)
        }

    }


    private fun newRequestWithAccessToken(request: Request, accessToken: String): Request {
        return request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }
}