package app.rootstock.data.messages

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource.LoadResult.Page.Companion.COUNT_UNDEFINED
import androidx.room.withTransaction
import app.rootstock.api.MessageService
import app.rootstock.api.SendMessage
import app.rootstock.data.db.AppDatabase
import app.rootstock.data.db.RemoteKeys
import app.rootstock.data.db.RemoteKeysDao
import app.rootstock.data.network.ResponseResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MessageRepository @Inject constructor(
    private val messageDao: MessageDao,
    private val messageService: MessageService,
    private val remoteKeysDao: RemoteKeysDao,
    private val database: AppDatabase
) {

    /**
     * Search messages for specified channel
     */
    fun getSearchResultStream(channelId: Long): Flow<PagingData<Message>> {
        val pagingSourceFactory = { messageDao.messagesInChannel(channelId) }

        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
                prefetchDistance = 30,
                maxSize = MAX_SIZE,
            ),
            remoteMediator = MessageRemoteMediator(
                channelId,
                messageService,
                remoteKeysDao,
                messageDao,
                database
            ),
            pagingSourceFactory = pagingSourceFactory,

        ).flow
    }

    suspend fun sendMessage(message: SendMessage): Flow<ResponseResult<Message?>> = flow {
        val response = messageService.sendMessages(message)
        val state = when (response.isSuccessful) {
            true -> {
                ResponseResult.success(response.body())
            }
            else -> ResponseResult.error(response.message())
        }

        if (response.isSuccessful) {
            response.body()?.let {
                database.withTransaction {
                    messageDao.insert(it.apply { channelId = message.channelId })
                    val lastKey = remoteKeysDao.getLastRemoteKeys(message.channelId)
                    val prevKey: Int?
                    val nextKey: Int?

                    if (lastKey == null) {
                        prevKey = null
                        nextKey = NETWORK_PAGE_SIZE
                    } else {
                        val count = remoteKeysDao.getRemoteKeysCount(channelId = message.channelId)
                        prevKey =
                            if (count % 100 == 0) (lastKey.prevKey?.plus(NETWORK_PAGE_SIZE)) else lastKey.prevKey
                        nextKey =
                            if (count % 100 == 0) (lastKey.nextKey?.plus(NETWORK_PAGE_SIZE)) else lastKey.nextKey
                    }
                    remoteKeysDao.insert(
                        RemoteKeys(
                            messageId = it.messageId,
                            channelId = message.channelId,
                            nextKey = nextKey,
                            prevKey = prevKey,
                        )
                    )
//                        channelLocal.insert(it)
//                        cacheCleaner.cleanCache()
                }
            }
        }
        emit(state)

    }.catch {
        emit(ResponseResult.error("Something went wrong!"))
    }


    companion object {
        const val NETWORK_PAGE_SIZE = 100

        // Use custom initial load size, because default is pageSize * 3
        private const val INITIAL_LOAD_SIZE = 0

        // todo: define based on android version due to performance?
        private const val MAX_SIZE = 450
    }
}