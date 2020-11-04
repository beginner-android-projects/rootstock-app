package app.rootstock.data.workspace

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "workspaces_tree",
    foreignKeys = [
        ForeignKey(
            entity = Workspace::class,
            parentColumns = ["ws_id"],
            childColumns = ["parent"]
        ),
        ForeignKey(
            entity = Workspace::class,
            parentColumns = ["ws_id"],
            childColumns = ["child"]
        )
    ]
)
data class WorkspaceTree(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "parent")
    val parent: String,
    @ColumnInfo(name = "child")
    val child: String,
)
