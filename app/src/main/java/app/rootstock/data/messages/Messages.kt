package app.rootstock.data.messages

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity
data class Message constructor(
    @PrimaryKey
    @SerializedName("message_id")
    val messageId: Long,
    val content: String,
    @SerializedName("created_at")
    val createdAt: Date,
    @SerializedName("message_type")
    val type: Short,
)

// todo: relation to channel