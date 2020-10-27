package app.rootstock.data.workspace

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "workspaces")
data class Workspace(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "name") val id: Int? = 0,
    @ColumnInfo(name = "name")
    @SerializedName("name")
    val name: String,
    @ColumnInfo(name = "background_color")
    @SerializedName("background_color")
    val backgroundColor: String,
    @ColumnInfo(name = "image_url")
    @SerializedName("image_url")
    val imageUrl: String?,
    @ColumnInfo(name = "ws_id")
    @SerializedName("ws_id")
    val workspaceId: String,
    @ColumnInfo(name = "channels")
    @SerializedName("channels")
    val channels: List<String>,
    @ColumnInfo(name = "children")
    @SerializedName("children")
    val children: List<String>,
)
//SELECT f.id, f.name FROM workspaces f JOIN workspaces_tree t ON t.child_id = f.id WHERE t.parent_id = 1;