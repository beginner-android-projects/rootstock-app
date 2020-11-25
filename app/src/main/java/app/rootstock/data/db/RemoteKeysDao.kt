package app.rootstock.data.db


import androidx.room.*
import app.rootstock.data.channel.Channel

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(entities: List<RemoteKeys>)

    @Transaction
    suspend fun upsertAll(entities: List<RemoteKeys>) {
        insertAll(entities)
        update(entities)
    }

    @Query("SELECT * FROM remote_keys WHERE channel_id = :channelId and message_id = :repoId")
    suspend fun remoteKeysMessageId(repoId: Long, channelId: Long): RemoteKeys?

}

