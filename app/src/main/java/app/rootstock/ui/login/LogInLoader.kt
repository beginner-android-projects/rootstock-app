package app.rootstock.ui.login

import app.rootstock.api.UserLogInService
import app.rootstock.data.token.Token
import app.rootstock.data.token.TokenNetwork
import app.rootstock.data.user.User
import app.rootstock.data.user.UserWithPassword
import retrofit2.Response
import javax.inject.Inject

class LogInLoader @Inject constructor(private val logInService: UserLogInService) {

    suspend fun logIn(user: UserWithPassword): Response<TokenNetwork> {
        return logInService.logIn(username = user.email, password = user.password)
    }

}
