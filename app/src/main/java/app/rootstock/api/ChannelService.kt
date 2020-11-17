package app.rootstock.api

import app.rootstock.data.channel.Channel
import app.rootstock.data.workspace.WorkspaceWithChildren
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ChannelService {

    @PATCH("/channels/{channelId}")
    suspend fun updateChannel(
        @Path("channelId") channelId: Long,
        @Body channel: Channel,
    ): Response<Channel>


    @DELETE("/channels/{channelId}")
    suspend fun deleteChannel(
        @Path("channelId") channelId: Long
    ): Response<Void>
}