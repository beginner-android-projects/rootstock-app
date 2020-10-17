package app.rootstock.data.user

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val userDao: UserDao) {

    fun getUser() = userDao.searchUser()

    suspend fun insertUser(user: User) = userDao.insert(user)
}