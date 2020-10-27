package app.rootstock.api

import app.rootstock.data.workspace.Workspace
import retrofit2.Response
import retrofit2.http.*


interface WorkspaceService {
    @GET("/workspaces/{workspaceId}")
    suspend fun getWorksapce(
        @Header("Authorization") accessToken: String,
        @Path("workspaceId") workspaceId: String
    ): Response<Workspace>
}
