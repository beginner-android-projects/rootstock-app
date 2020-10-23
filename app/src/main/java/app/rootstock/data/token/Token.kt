package app.rootstock.data.token

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import app.rootstock.data.db.TokenConverters
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "token")
@TypeConverters(TokenConverters::class)
data class Token constructor(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int? = 1,
    @ColumnInfo(name = "access_token") val accessToken: String,
    @ColumnInfo(name = "refresh_token") val refreshToken: String,
    @ColumnInfo(name = "token_type") val tokenType: String,
    // store also time of creation to detect old ones
    val creationTime: Date? = Date()
)

data class TokenNetwork constructor(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("token_type") val tokenType: String,
)

object TokenConstants {
    const val ACCESS_TOKEN_LIFETIME_MINS = 20 // 20 min
    const val REFRESH_TOKEN_LIFETIME_MINS = 21 * 7 * 24 * 60 // 3 weeks
}

