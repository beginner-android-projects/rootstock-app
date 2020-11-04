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

}