package app.rootstock.data.channel

import app.rootstock.api.ChannelService
import app.rootstock.api.ColorService
import app.rootstock.data.network.ResponseResult
import app.rootstock.data.prefs.CacheClass
import app.rootstock.data.prefs.SharedPrefsController
import app.rootstock.ui.channels.ChannelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

data class ImageUrls(val urls: List<String>)

class ColorsDelegate @Inject constructor(private val colorService: ColorService) {

    suspend fun getColors(): Flow<ResponseResult<ImageUrls?>> = flow {
        val channelResponse = colorService.getColors()

        val state = when (channelResponse.isSuccessful) {
            true -> {
                ResponseResult.success(channelResponse.body())
            }
            else -> ResponseResult.error(channelResponse.message())
        }
        emit(state)

    }.catch {
        emit(ResponseResult.error("Something went wrong!"))
    }

}