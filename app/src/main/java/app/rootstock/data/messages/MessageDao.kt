package app.rootstock.data.messages

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<Message>)

    @Query("SELECT * FROM messages where channel_id = :channelId order by created_at desc")
    fun messagesInChannel(channelId: Long): PagingSource<Int, Message>

    @Query("DELETE FROM messages")
    suspend fun clearRepos()

}