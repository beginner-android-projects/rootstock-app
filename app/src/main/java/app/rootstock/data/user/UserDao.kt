package app.rootstock.data.user

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Query("select * from user limit 1")
    fun searchUser(): LiveData<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long
}


//SELECT f.id, f.name FROM workspaces f JOIN workspaces_tree t ON t.child_id = f.id WHERE t.parent_id = 1;