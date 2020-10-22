package app.rootstock.data.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * User class for local room database
 */
@Entity
class User(
    @SerializedName("user_id")
    @ColumnInfo(name = "user_id")
    @PrimaryKey
    val userId: String,

    @SerializedName("email")
    @ColumnInfo(name = "email")
    val email: String

)