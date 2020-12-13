package app.rootstock.data.channel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "channels_favourite",
    foreignKeys = [ForeignKey(
        entity = Channel::class,
        childColumns = ["channel_id"],
        parentColumns = ["channel_id"],
        onDelete = CASCADE
    )]
)
data class ChannelFavourite constructor(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "channel_id") val channelId: Long
)