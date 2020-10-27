package app.rootstock.ui.workspace

import app.rootstock.data.workspace.WorkspaceDao
import javax.inject.Inject

class WorkspaceRepository @Inject constructor(
    private val workspaceDataSource: WorkspaceDataSource,
    private val workspaceDao: WorkspaceDao
) {

//    fun getWorkspace(workspaceId: String) = workspaceDataSource.getWorkspace(workspaceId)

    fun getMainWorkspace() = workspaceDao.getMainWorkspace()


}