package app.rootstock.api

import app.rootstock.data.token.TokenNetwork
import app.rootstock.data.user.User
import retrofit2.Response
import retrofit2.http.*

data class UserSignUpModel(val email: String, val password: String)

interface UserSignUpService {
    @POST("/users/create")
    suspend fun createUser(@Body userSignUp: UserSignUpModel): Response<User>
}

interface UserLogInService {
    @POST("/authenticate")
    @FormUrlEncoded
    suspend fun logIn(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<TokenNetwork>

}