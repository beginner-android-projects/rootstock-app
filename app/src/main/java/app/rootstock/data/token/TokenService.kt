package app.rootstock.data.token

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface TokenService {
    @POST("/refresh-token")
    suspend fun refreshToken(
        @Body tokenUpdate: TokenUpdate
    ): Response<Token>

    @POST("/revoke-refresh")
    suspend fun revokeToken(
        @Body tokenRevoke: TokenRevoke,
    ): Response<Any>
}

