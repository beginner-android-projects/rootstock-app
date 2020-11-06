package app.rootstock.data.token

import app.rootstock.data.user.UserRepository
import app.rootstock.exceptions.NoUserException
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

interface TokenRepository {
    suspend fun insertToken(token: Token)
    suspend fun updateToken(token: Token)
    suspend fun getAccessToken(): String?
    suspend fun getToken(): Token?
    suspend fun getRefreshToken(): String?
    suspend fun getTokenFromNetwork(refreshToken: String): Response<Token>
    suspend fun revokeToken(token: String)
}


@Singleton
class TokenRepositoryImpl @Inject constructor(
    private val tokenLocalSource: TokenDao,
    private val tokenRemote: TokenService,
    private val userRepository: UserRepository,
) :
    TokenRepository {

    override suspend fun insertToken(token: Token) = tokenLocalSource.deleteAndInsert(token)

    override suspend fun updateToken(token: Token) = tokenLocalSource.updateToken(token)

    override suspend fun getAccessToken() = tokenLocalSource.searchAccessToken()

    override suspend fun getToken() = tokenLocalSource.getToken()

    override suspend fun getRefreshToken() = tokenLocalSource.searchRefreshToken()

    override suspend fun getTokenFromNetwork(refreshToken: String): Response<Token> {
        val userId = userRepository.getUserId() ?: throw NoUserException()
        return tokenRemote.refreshToken(
            TokenUpdate(
                refreshToken = refreshToken,
                userId = userId
            )
        )
    }

    override suspend fun revokeToken(token: String) {
        tokenRemote.revokeToken(tokenRevoke = TokenRevoke(token))
    }


}