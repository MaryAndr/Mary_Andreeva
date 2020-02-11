package ru.filit.motiv.app.oauth

import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.filit.motiv.app.presenters.interactors.UserInteractor
import ru.filit.motiv.app.utils.Constants
import ru.filit.motiv.app.utils.PreferenceHelper
import ru.filit.motiv.app.utils.PreferenceHelper.set
import ru.filit.motiv.app.utils.PreferenceHelper.get
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

            val refreshedToken = UserInteractor(ctx).userService.auth(
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