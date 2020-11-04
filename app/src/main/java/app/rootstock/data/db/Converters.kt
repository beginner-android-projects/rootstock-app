package app.rootstock.data.db

import androidx.room.TypeConverter
import java.util.*

class TokenConverters {
    @TypeConverter
    fun toDate(dateLong: Long): Date {
        return Date(dateLong)
    }

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time;
    }
}
