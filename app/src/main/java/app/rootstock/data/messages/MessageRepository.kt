package app.rootstock.data.messages

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.rootstock.api.MessageService
import app.rootstock.data.db.AppDatabase
import app.rootstock.data.db.RemoteKeysDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MessageRepository @Inject constructor(
    private val messageDao: MessageDao,
    private val messageService: MessageService,
    private val remoteKeysDao: RemoteKeysDao,
    private val database: AppDatabase
) {

    /**
     * Search repositories whose names match the query, exposed as a stream of data that will emit
     * every time we get more data from the network.
     */
    @ExperimentalPagingApi
    fun getSearchResultStream(channelId: Long): Flow<PagingData<Message>> {
        val pagingSourceFactory = { messageDao.messagesInChannel(channelId) }

        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false, prefetchDistance = 30, initialLoadSize = INITIAL_LOAD_SIZE, maxSize = MAX_SIZE),
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

    companion object {
        private const val NETWORK_PAGE_SIZE = 100
        // Use custom initial load size, because default is pageSize * 3
        private const val INITIAL_LOAD_SIZE = 150
        // todo: define based on android version due to performance?
        private const val MAX_SIZE = 450
    }
}