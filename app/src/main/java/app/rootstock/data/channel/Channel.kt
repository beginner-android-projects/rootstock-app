package app.rootstock.data.channel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import app.rootstock.data.workspace.Workspace
import com.google.gson.annotations.SerializedName

interface ChannelI {
    val name: String
    val channelId: Long
    val lastMessage: String?
    var workspaceId: String?
    val imageUrl: String?
    val backgroundColor: String?

}

@Entity(
    tableName = "channels",
    foreignKeys = [ForeignKey(
        entity = Workspace::class,
        parentColumns = ["ws_id"],
        childColumns = ["workspace_id"]
    )]
)
data class Channel(
    @SerializedName("name")
    @ColumnInfo(name = "name")
    override val name: String,
    @PrimaryKey
    @ColumnInfo(name = "channel_id")
    @SerializedName("channel_id")
    override val channelId: Long,
    @ColumnInfo(name = "last_message")
    @SerializedName("last_message")
    override val lastMessage: String?,
    @ColumnInfo(name = "background_color")
    @SerializedName("background_color")
    override val backgroundColor: String,
    @ColumnInfo(name = "image_url")
    @SerializedName("image_url")
    override val imageUrl: String?,
    @ColumnInfo(name = "last_update")
    @SerializedName("last_update")
    val lastUpdate: String,
    @ColumnInfo(name = "workspace_id")
    @SerializedName("workspace_id")
    override var workspaceId: String? = null
) : ChannelI

