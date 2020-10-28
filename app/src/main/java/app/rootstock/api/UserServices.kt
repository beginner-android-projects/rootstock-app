package app.rootstock.api

import app.rootstock.data.token.TokenNetwork
import app.rootstock.data.user.User
import app.rootstock.data.user.UserWithPassword
import app.rootstock.ui.signup.SignUpUser
import retrofit2.Response
import retrofit2.http.*

interface UserSignUpService {
    @POST("/users/create")
    suspend fun createUser(@Body userSignUp: SignUpUser): Response<User>
}

interface UserLogInService {
    @POST("/authenticate")
    @FormUrlEncoded
    suspend fun logIn(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<TokenNetwork>

}