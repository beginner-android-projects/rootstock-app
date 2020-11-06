package app.rootstock.ui.signup

import app.rootstock.api.UserInfoService
import app.rootstock.api.UserLogInService
import app.rootstock.api.UserSignUpService
import app.rootstock.data.network.ResponseResult
import app.rootstock.data.token.Token
import app.rootstock.data.user.User
import app.rootstock.data.user.UserWithPassword
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface AccountRepository {
    suspend fun register(user: SignUpUser): Flow<ResponseResult<User?>>
    suspend fun authenticate(user: UserWithPassword): Flow<ResponseResult<Token?>>
    suspend fun getUserRemote(token: String): Flow<ResponseResult<User?>>
}

/**
 * Repository for user account manipulation
 */
class AccountRepositoryImpl @Inject constructor(
    private val signUpService: UserSignUpService,
    private val logInService: UserLogInService,
    private val userInfoService: UserInfoService,
): AccountRepository {

    override suspend fun register(user: SignUpUser): Flow<ResponseResult<User?>> = flow {
        val tokenResponse = signUpService.createUser(user)

        val state = when (tokenResponse.isSuccessful) {
            true -> ResponseResult.success(tokenResponse.body())
            else -> ResponseResult.error(tokenResponse.message())
        }
        emit(state)
    }.catch {
        emit(ResponseResult.error("Something went wrong!"))
    }


    override suspend fun authenticate(user: UserWithPassword): Flow<ResponseResult<Token?>> = flow {
        val tokenResponse = logInService.logIn(username = user.email, password = user.password)

        val state = when (tokenResponse.isSuccessful) {
            true -> ResponseResult.success(tokenResponse.body())
            else -> ResponseResult.error(tokenResponse.message())
        }
        emit(state)
    }.catch {
        emit(ResponseResult.error("Something went wrong!"))
    }

    /**
     * requests user_id and email after login in case of email update
     */
    override suspend fun getUserRemote(token: String): Flow<ResponseResult<User?>> = flow {
        val userResponse = userInfoService.getUser("Bearer $token")

        val state = when (userResponse.isSuccessful) {
            true -> ResponseResult.success(userResponse.body())
            else -> ResponseResult.error(userResponse.message())
        }

        emit(state)
    }.catch {
        emit(ResponseResult.error("Something went wrong!"))
    }

}