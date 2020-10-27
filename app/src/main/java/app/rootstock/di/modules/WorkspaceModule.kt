package app.rootstock.di.modules

import app.rootstock.data.db.AppDatabase
import app.rootstock.data.workspace.WorkspaceDao
import app.rootstock.ui.workspace.WorkspaceDataSource
import app.rootstock.ui.workspace.WorkspaceRepository
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
    fun provideWorkspaceDataSource(retrofit: Retrofit): WorkspaceDataSource {
//        retrofit.create(WorkspaceService::class.java)
        return WorkspaceDataSource()
    }

    @Provides
    fun provideWorkspaceRepository(
        workspaceDataSource: WorkspaceDataSource,
        workspaceDao: WorkspaceDao
    ): WorkspaceRepository {
        return WorkspaceRepository(workspaceDataSource, workspaceDao)
    }
}