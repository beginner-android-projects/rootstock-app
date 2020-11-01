package app.rootstock.ui.signup

import app.rootstock.api.UserInfoService
import app.rootstock.api.UserLogInService
import app.rootstock.api.UserSignUpService
import app.rootstock.data.network.ResponseResult
import app.rootstock.data.token.TokenNetwork
import app.rootstock.data.user.User
import app.rootstock.data.user.UserWithPassword
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

/**
 * Repository for user account manipulation
 */
class AccountRepository @Inject constructor(
    private val signUpService: UserSignUpService,
    private val logInService: UserLogInService,
    private val userInfoService: UserInfoService,
) {

    suspend fun register(user: SignUpUser): Flow<ResponseResult<User?>> = flow {
        val tokenResponse = signUpService.createUser(user)

        val state = when (tokenResponse.isSuccessful) {
            true -> ResponseResult.success(tokenResponse.body())
            else -> ResponseResult.error(tokenResponse.message())
        }
        emit(state)
    }.catch {
        emit(ResponseResult.error("Something went wrong!"))
    }


    suspend fun authenticate(user: UserWithPassword): Flow<ResponseResult<TokenNetwork?>> = flow {
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
    suspend fun getUserRemote(token: String): Flow<ResponseResult<User?>> = flow {
        val userResponse = userInfoService.getUser(token)

        val state = when (userResponse.isSuccessful) {
            true -> ResponseResult.success(userResponse.body())
            else -> ResponseResult.error(userResponse.message())
        }

        emit(state)
    }.catch {
        emit(ResponseResult.error("Something went wrong!"))
    }

}