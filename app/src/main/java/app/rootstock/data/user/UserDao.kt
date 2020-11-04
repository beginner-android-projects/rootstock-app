package app.rootstock.data.user

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Query("select * from users limit 1")
    fun searchUser(): LiveData<User?>

    @Query("select user_id from users limit 1")
    suspend fun getUserId(): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long
}