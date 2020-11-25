package app.rootstock.api

import app.rootstock.data.messages.Message
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MessageService {

    @GET("/messages/{channelId}")
    suspend fun getMessages(
        @Path("channelId") channelId: Long,
        @Query("offset") offset: Int = 0,
    ): List<Message>
}

data class MessageSearchResponse constructor(
    val messages: List<Message> = emptyList()
)