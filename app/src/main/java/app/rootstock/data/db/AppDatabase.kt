package app.rootstock.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import app.rootstock.data.channel.Channel
import app.rootstock.data.channel.ChannelDao
import app.rootstock.data.messages.Message
import app.rootstock.data.messages.MessageDao
import app.rootstock.data.token.Token
import app.rootstock.data.token.TokenDao
import app.rootstock.data.user.User
import app.rootstock.data.user.UserDao
import app.rootstock.data.workspace.Workspace
import app.rootstock.data.workspace.WorkspaceDao
import app.rootstock.data.workspace.WorkspaceTree
import app.rootstock.utils.DATABASE_NAME

/**
 * The Room database for this app
 */
@Database(
    entities = [
        User::class, Token::class, Workspace::class, Channel::class, WorkspaceTree::class,
        Message::class,
        RemoteKeys::class
    ], version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tokenDao(): TokenDao
    abstract fun workspaceDao(): WorkspaceDao
    abstract fun messageDao(): MessageDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun channelDao(): ChannelDao

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
                .addCallback(CALLBACK_UPDATE_LAST_MESSAGE)
                .build()
        }

        private val CALLBACK_UPDATE_LAST_MESSAGE = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("create trigger update_last_message after insert on messages begin update channels set last_message = new.content where channel_id = new.channel_id; end;")
            }
        }
    }
}