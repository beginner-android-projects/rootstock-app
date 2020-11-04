package app.rootstock.di.modules

import app.rootstock.api.WorkspaceService
import app.rootstock.data.channel.ChannelDao
import app.rootstock.data.db.AppDatabase
import app.rootstock.data.workspace.WorkspaceDao
import app.rootstock.ui.workspace.WorkspaceRepository
import app.rootstock.ui.workspace.WorkspaceRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import retrofit2.Retrofit


/**
 * Module for WorkspaceActivity
 */
@InstallIn(ActivityComponent::class)
@Module
object WorkspaceModule {

    @Provides
    fun provideWorkspaceDao(appDatabase: AppDatabase): WorkspaceDao {
        return appDatabase.workspaceDao()
    }

    @Provides
    fun provideChannelDao(appDatabase: AppDatabase): ChannelDao {
        return appDatabase.channelDao()
    }


    @Provides
    fun provideWorkspaceService(retrofit: Retrofit): WorkspaceService {
        return retrofit.create(WorkspaceService::class.java)
    }

    @Provides
    fun provideWorkspaceRepository(
        workspaceDataSource: WorkspaceService,
        workspaceDao: WorkspaceDao,
        channelDao: ChannelDao,
    ): WorkspaceRepository {
        return WorkspaceRepositoryImpl(workspaceDataSource, workspaceDao, channelDao)
    }
}