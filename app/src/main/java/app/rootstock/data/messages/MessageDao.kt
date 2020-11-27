package app.rootstock.data.messages

import android.database.Cursor
import androidx.paging.DataSource
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.paging.LimitOffsetDataSource
import androidx.room.util.CursorUtil
import java.util.*


@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<Message>)

    @Query("SELECT * FROM messages where channel_id = :channelId order by created_at desc")
    fun messagesInChannel(channelId: Long): PagingSource<Int, Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: Message)

}

//class PS<K : Any, V : Any> : PagingSource<K, V>() {
//    override val jumpingSupported: Boolean
//        get() = false
//    override suspend fun load(params: LoadParams<K>): LoadResult<K, V> {
//    }
//
//}