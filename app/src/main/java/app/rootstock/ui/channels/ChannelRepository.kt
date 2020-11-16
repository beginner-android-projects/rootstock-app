package app.rootstock.ui.channels

import app.rootstock.api.ChannelService
import app.rootstock.data.channel.Channel
import app.rootstock.data.channel.ChannelDao
import app.rootstock.data.network.CacheCleaner
import app.rootstock.data.network.ResponseResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class ChannelRepositoryImpl @Inject constructor(
    private val channelRemoteSource: ChannelService,
    private val channelLocal: ChannelDao,
    private val cacheCleaner: CacheCleaner,
) {

    fun updateChannel(channel: Channel): Flow<ResponseResult<Channel?>> = flow {
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

    private suspend fun updateLocal(channel: Channel?) {
        channel ?: return
        channelLocal.update(channel)
    }

}