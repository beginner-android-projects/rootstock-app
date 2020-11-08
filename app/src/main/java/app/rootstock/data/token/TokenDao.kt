package app.rootstock.data.token

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface TokenDao {

    @Transaction
    suspend fun deleteAndInsert(token: Token) {
        deleteAll()
        insertToken(token)
    }

    @Query("select access_token from token limit 1")
    suspend fun searchAccessToken(): String?

    @Query("select * from token limit 1")
    suspend fun getToken(): Token?

    @Query("select refresh_token from token limit 1")
    suspend fun searchRefreshToken(): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToken(token: Token)

    @Query("delete from token")
    suspend fun deleteAll()

}