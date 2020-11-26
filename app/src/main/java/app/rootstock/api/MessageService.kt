package app.rootstock.api

import app.rootstock.data.messages.Message
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

interface MessageService {

    @GET("/messages/{channelId}")
    suspend fun getMessages(
        @Path("channelId") channelId: Long,
        @Query("offset") offset: Int = 0,
    ): List<Message>

    @POST("/messages/")
    suspend fun sendMessages(
        @Body sendMessage: SendMessage,
    ): Response<Message>
}

data class SendMessage(
    val content: String,
    @SerializedName("channel_id_to_add_to") val channelId: Long
)