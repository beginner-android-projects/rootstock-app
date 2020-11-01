package app.rootstock.data.network

import app.rootstock.data.user.UserRepository
import kotlinx.coroutines.*
import okhttp3.*
import javax.inject.Inject

class TokenInterceptor @Inject constructor(private val userRepository: UserRepository) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            userRepository.getAccessToken()
        }
        token ?: return chain.proceed(chain.request())
        val request = createRequestWithAccessToken(chain.request(), token)
        return chain.proceed(request)
    }

    private fun createRequestWithAccessToken(request: Request, token: String): Request {
        return request.newBuilder().header("Authorization", "Bearer $token").build()
    }


}

class ServerAuthenticator @Inject constructor() : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        TODO()
    }

}