package app.rootstock.data.workspace

import androidx.room.*
import app.rootstock.data.channel.Channel
import com.google.gson.annotations.SerializedName

interface WorkspaceI {
    val workspaceId: String
    val name: String
    val imageUrl: String?
    val backgroundColor: String
}

@Entity(
    tableName = "workspaces",
    indices = [Index("ws_id")],
)
data class Workspace(
    @PrimaryKey
    @ColumnInfo(name = "ws_id")
    @SerializedName("ws_id")
    override val workspaceId: String,
    @ColumnInfo(name = "name")
    @SerializedName("name")
    override val name: String,
    @ColumnInfo(name = "background_color")
    @SerializedName("background_color")
    override val backgroundColor: String,
    @ColumnInfo(name = "image_url")
    @SerializedName("image_url")
    override val imageUrl: String?,
): WorkspaceI

/**
 * This class represents 1:m relationship in Workspace - Channels tables (Needed for Room)
 */
data class WorkspaceWithChannels(
    @Embedded val workspace: Workspace,
    @Relation(
        parentColumn = "ws_id",
        entityColumn = "workspace_id"
    )
    val channels: List<Channel>,
)


data class WorkspaceWithChildren(
    @SerializedName("name")
    override val name: String,
    @SerializedName("background_color")
    override val backgroundColor: String,
    @SerializedName("image_url")
    override val imageUrl: String?,
    @SerializedName("ws_id")
    override val workspaceId: String,
    @SerializedName("channels")
    val channels: List<Channel>,
    @SerializedName("children")
    val children: List<Workspace>,
): WorkspaceI