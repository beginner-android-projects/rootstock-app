package app.rootstock.data.token

import javax.inject.Inject
import javax.inject.Singleton

interface TokenRepository {
    suspend fun updateAccessToken(accessToken: String)
    suspend fun updateRefreshToken(refreshToken: String)
    suspend fun insertToken(token: Token): Long
    suspend fun getAccessToken(): String?
}


@Singleton
class TokenRepositoryImpl @Inject constructor(private val tokenDao: TokenDao): TokenRepository {

    override suspend fun updateAccessToken(accessToken: String) = tokenDao.updateAccessToken(accessToken)

    override suspend fun updateRefreshToken(refreshToken: String) = tokenDao.updateRefreshToken(refreshToken)

    override suspend fun insertToken(token: Token) = tokenDao.insertToken(token)

    override suspend fun getAccessToken() = tokenDao.searchAccessToken()
}