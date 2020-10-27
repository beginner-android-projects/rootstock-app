package app.rootstock.data.token

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TokenDao {
    @Query("select * from token limit 1")
    fun searchToken(): LiveData<Token?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToken(token: Token): Long

    @Query("update token set access_token = :token where id = 1")
    suspend fun updateAccessToken(token: String)

    @Query("update token set refresh_token = :token where id = 1")
    suspend fun updateRefreshToken(token: String)
}