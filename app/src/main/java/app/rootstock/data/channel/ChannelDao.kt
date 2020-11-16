package app.rootstock.data.channel

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface ChannelDao {
    // select root workspace for user
    @Query("select * from channels where channel_id = :id;")
    fun getChannelById(id: Long): Flow<Channel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(channel: Channel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    @Transaction
    suspend fun insertAll(channels: List<Channel>)

    @Update
    suspend fun update(channel: Channel)

}