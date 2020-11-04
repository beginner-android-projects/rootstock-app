package app.rootstock.ui.workspace

import android.util.Log
import app.rootstock.api.WorkspaceService
import app.rootstock.data.channel.ChannelDao
import app.rootstock.data.network.NetworkBoundRepository
import app.rootstock.data.workspace.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class WorkspaceRepository @Inject constructor(
    private val workspaceRemoteSource: WorkspaceService,
    private val workspaceLocal: WorkspaceDao,
    private val channelLocal: ChannelDao,
) {

    @ExperimentalCoroutinesApi
    suspend fun getMainWorkspace(workspaceId: String) =
        object : NetworkBoundRepository<WorkspaceWithChildren?, WorkspaceWithChildren>() {
            override suspend fun persistData(response: WorkspaceWithChildren) = response
                .let {
                    // insert workspace
                    workspaceLocal.insert(
                        Workspace(
                            name = it.name,
                            workspaceId = it.workspaceId,
                            backgroundColor = it.backgroundColor,
                            imageUrl = it.imageUrl,
                        )
                    )
                    // insert children workspaces
                    workspaceLocal.insertAll(it.children)

                    // insert channels
                    channelLocal.insertAll(it.channels.onEach { channel ->
                        channel.workspaceId = it.workspaceId
                    })

                    // create hierarchy
                    val list = mutableListOf<WorkspaceTree>()
                    it.children.forEach { child ->
                        list.add(
                            WorkspaceTree(
                                parent = it.workspaceId,
                                child = child.workspaceId
                            )
                        )
                    }
                    workspaceLocal.insertHierarchy(list)
                }

            override suspend fun fetchFromLocal(): Flow<WorkspaceWithChildren?> {
                val workspaces = workspaceLocal.getChildrenWorkspacesById(workspaceId)
                val workspaceWithChannels =
                    workspaceLocal.getWorkspaceWithChannels(workspaceId) ?: return flow { emit(null) }
                return flow {
                    emit(
                        WorkspaceWithChildren(
                            workspaceId = workspaceWithChannels.workspace.workspaceId,
                            name = workspaceWithChannels.workspace.name,
                            imageUrl = workspaceWithChannels.workspace.imageUrl,
                            backgroundColor = workspaceWithChannels.workspace.backgroundColor,
                            children = workspaces,
                            channels = workspaceWithChannels.channels
                        )
                    )
                }
            }

            override suspend fun fetchFromRemote(): WorkspaceWithChildren {
                val workspaceResponse = workspaceRemoteSource.getWorkspace(workspaceId)
                Log.d("123 ----- remote -----", "${workspaceResponse.body()}")
                return workspaceResponse.body()!!
            }
        }.asFlow()


}
