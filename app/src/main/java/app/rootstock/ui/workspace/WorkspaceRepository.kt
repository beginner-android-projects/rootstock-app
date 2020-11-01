package app.rootstock.ui.workspace

import android.util.Log
import app.rootstock.api.WorkspaceService
import app.rootstock.data.network.NetworkBoundRepository
import app.rootstock.data.workspace.Workspace
import app.rootstock.data.workspace.WorkspaceDao
import app.rootstock.data.workspace.WorkspaceNetworkResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class WorkspaceRepository @Inject constructor(
    private val workspaceRemoteSource: WorkspaceService,
    private val workspaceLocal: WorkspaceDao
) {

    @ExperimentalCoroutinesApi
    suspend fun getMainWorkspace(workspaceId: String) =
        object : NetworkBoundRepository<Workspace, WorkspaceNetworkResponse>() {
            override suspend fun persistData(response: WorkspaceNetworkResponse) = response
                .let {
                    workspaceLocal.insert(
                        Workspace(
                            name = it.name,
                            backgroundColor = it.backgroundColor,
                            imageUrl = it.imageUrl,
                            workspaceId = it.workspaceId
                        )
                    )
                }

            override fun fetchFromLocal(): Flow<Workspace> = workspaceLocal.getMainWorkspace()

            override suspend fun fetchFromRemote(): WorkspaceNetworkResponse {
                val a = workspaceRemoteSource.getWorkspace(workspaceId)
                return a.body()!!
            }
        }.asFlow()


}

