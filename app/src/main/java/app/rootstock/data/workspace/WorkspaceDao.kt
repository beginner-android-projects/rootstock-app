package app.rootstock.data.workspace

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceDao {
    // select root workspace for user
    @Query("select * from workspaces, users where workspaces.ws_id = users.user_id;")
    fun getMainWorkspace(): Flow<Workspace>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workspace: Workspace)
}
