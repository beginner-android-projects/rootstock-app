package app.rootstock.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import app.rootstock.data.token.Token
import app.rootstock.data.token.TokenDao
import app.rootstock.data.user.User
import app.rootstock.data.user.UserDao
import app.rootstock.data.workspace.Workspace
import app.rootstock.data.workspace.WorkspaceDao
import app.rootstock.utils.DATABASE_NAME

/**
 * The Room database for this app
 */
@Database(entities = [User::class, Token::class, Workspace::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tokenDao(): TokenDao
    abstract fun workspaceDao(): WorkspaceDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(
                        context
                    ).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}