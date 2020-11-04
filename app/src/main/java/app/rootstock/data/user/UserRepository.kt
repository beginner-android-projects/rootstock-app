package app.rootstock.data.user

import app.rootstock.data.token.Token
import app.rootstock.data.token.TokenDao
import javax.inject.Inject
import javax.inject.Singleton

// todo token repository
@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val tokenDao: TokenDao
) {

    fun getUser() = userDao.searchUser()

    suspend fun getUserId() = userDao.getUserId()

    suspend fun insertUser(user: User) = userDao.insert(user)

    suspend fun insertToken(token: Token) = tokenDao.insertToken(token)

    suspend fun getAccessToken() = tokenDao.searchAccessToken()

    suspend fun updateAccessToken(accessToken: String) = tokenDao.updateAccessToken(accessToken)

    suspend fun updateRefreshToken(refreshToken: String) = tokenDao.updateRefreshToken(refreshToken)


}