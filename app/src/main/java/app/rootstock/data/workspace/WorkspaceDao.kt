package app.rootstock.data.workspace

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceDao {
    // select root workspace for user
    @Query("select ws_id, name, background_color, image_url from workspaces as w inner join users on w.ws_id = users.user_id limit 1")
    fun getMainWorkspace(): Flow<Workspace>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workspace: Workspace)

    @Transaction
    @Query("select * from workspaces where ws_id = :id limit 1")
    suspend fun getWorkspaceWithChannels(id: String): WorkspaceWithChannels?

    @Query("select * from workspaces where ws_id in (select child from workspaces_tree where parent = :id);")
    suspend fun getChildrenWorkspacesById(id: String): List<Workspace>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    @Transaction
    suspend fun insertHierarchy(hierarchy: List<WorkspaceTree>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    @Transaction
    suspend fun insertAll(children: List<Workspace>)


}
