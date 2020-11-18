package app.rootstock.ui.channels

import app.rootstock.api.ChannelService
import app.rootstock.data.channel.Channel
import app.rootstock.data.channel.ChannelDao
import app.rootstock.data.channel.CreateChannelRequest
import app.rootstock.data.network.CacheCleaner
import app.rootstock.data.network.ResponseResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface ChannelRepository {
    suspend fun updateChannel(channel: Channel): Flow<ResponseResult<Channel?>>
    suspend fun deleteChannel(channelId: Long): Flow<ResponseResult<Void?>>
    suspend fun createChannel(channel: CreateChannelRequest): Flow<ResponseResult<Channel?>>
}

class ChannelRepositoryImpl @Inject constructor(
    private val channelRemoteSource: ChannelService,
    private val channelLocal: ChannelDao,
    private val cacheCleaner: CacheCleaner,
) : ChannelRepository {

    override suspend fun updateChannel(channel: Channel): Flow<ResponseResult<Channel?>> = flow {
        var isSuccess = false
        val channelResponse =
            channelRemoteSource.updateChannel(channelId = channel.channelId, channel = channel)

        val state = when (channelResponse.isSuccessful) {
            true -> {
                isSuccess = true; ResponseResult.success(channelResponse.body())
            }
            else -> ResponseResult.error(channelResponse.message())
        }
        if (isSuccess) {
            updateLocal(channelResponse.body())
            cacheCleaner.cleanCache()
        }
        emit(state)

    }.catch {
        emit(ResponseResult.error("Something went wrong!"))
    }

    override suspend fun deleteChannel(channelId: Long): Flow<ResponseResult<Void?>> = flow {
        var success = false
        val channelResponse =
            channelRemoteSource.deleteChannel(channelId = channelId)

        val state = when (channelResponse.isSuccessful) {
            true -> {
                success = true; ResponseResult.success(channelResponse.body())
            }
            else -> ResponseResult.error(channelResponse.message())
        }
        if (success) {
            channelLocal.deleteChannel(channelId)
            cacheCleaner.cleanCache()
        }
        emit(state)

    }.catch {
        emit(ResponseResult.error("Something went wrong!"))
    }

    override suspend fun createChannel(channel: CreateChannelRequest): Flow<ResponseResult<Channel?>> =
        flow {
            var success = false
            val channelResponse =
                channelRemoteSource.createChannel(channel = channel)

            val state = when (channelResponse.isSuccessful) {
                true -> {
                    success = true; ResponseResult.success(channelResponse.body())
                }
                else -> ResponseResult.error(channelResponse.message())
            }
            if (success) {
                channelResponse.body()?.let {
                    channelLocal.insert(it)
                    cacheCleaner.cleanCache()
                }
            }
            emit(state)

        }.catch {
            emit(ResponseResult.error("Something went wrong!"))
        }


    private suspend fun updateLocal(channel: Channel?) {
        channel ?: return
        channelLocal.update(channel)
    }

}