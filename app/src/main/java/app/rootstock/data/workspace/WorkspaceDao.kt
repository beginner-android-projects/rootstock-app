package app.rootstock.data.workspace

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.rootstock.data.user.User

@Dao
interface WorkspaceDao {
    // select root workspace for user
    @Query("select * from workspaces where ws_id = (select user_id from users limit 1)")
    fun getMainWorkspace(): LiveData<Workspace?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long
}
