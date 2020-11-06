package app.rootstock.data.network

import app.rootstock.data.token.TokenRepository
import app.rootstock.exceptions.NoUserException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ServerAuthenticator @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val tokenInterceptor: TokenInterceptor,
    private val reLogInObservable: ReLogInObservable,
) : Authenticator {

    private var currentRefreshToken: String? = null

    override fun authenticate(route: Route?, response: Response): Request? {
        // wrong access token
        if (!response.request().header("Authorization")
                .equals("Bearer " + tokenInterceptor.currentToken)
        ) {
            return null
        }

        // get refresh token from db
        val refreshToken = runBlocking {
            tokenRepository.getRefreshToken()
        }
        if (refreshToken == null) {
            relogin()
            return null
        }

        // refresh token
        val newTokenResponse = try {
            runBlocking {
                tokenRepository.getTokenFromNetwork(refreshToken)
            }
        } catch (e: NoUserException) {
            relogin()
            return null
        }

        val newToken = newTokenResponse.body()

        // refresh token has expired
        if (newTokenResponse.code() == 401 || newToken == null) {
            relogin()
            return null
        }

        tokenInterceptor.currentToken = newToken.accessToken
        currentRefreshToken = newToken.refreshToken

        // update token in db
        runBlocking {
            tokenRepository.insertToken(newToken)
        }

        // revoke old token
        // todo: inject coroutine context
        CoroutineScope(Dispatchers.Main).launch {
            tokenRepository.revokeToken(refreshToken)
        }
        return response.request().newBuilder()
            .header("Authorization", "Bearer ${newToken.accessToken}").build()
    }

    private fun relogin() {
        reLogInObservable.notifyObservers()
    }


}