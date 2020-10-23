package app.rootstock.data.workspace

import com.google.gson.annotations.SerializedName

data class Workspace(
    @SerializedName("name")
    val name: String,
    @SerializedName("background_color")
    val backgroundColor: String,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("ws_id")
    val workspaceId: String,
    @SerializedName("channels")
    val channels: List<String>,
    @SerializedName("children")
    val children: List<String>,
)
//SELECT f.id, f.name FROM workspaces f JOIN workspaces_tree t ON t.child_id = f.id WHERE t.parent_id = 1;