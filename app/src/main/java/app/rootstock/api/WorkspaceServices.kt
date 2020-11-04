package app.rootstock.api

import app.rootstock.data.workspace.WorkspaceWithChildren
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface WorkspaceService {
    @GET("/workspaces/{workspaceId}")
    suspend fun getWorkspace(
        @Path("workspaceId") workspaceId: String
    ): Response<WorkspaceWithChildren>
}