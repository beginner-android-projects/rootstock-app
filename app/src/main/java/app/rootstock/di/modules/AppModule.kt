package app.rootstock.di.modules


import android.content.Context
import app.rootstock.api.UserInfoService
import app.rootstock.data.db.AppDatabase
import app.rootstock.data.network.TokenInterceptor
import app.rootstock.data.token.TokenDao
import app.rootstock.data.token.TokenRepository
import app.rootstock.data.token.TokenRepositoryImpl
import app.rootstock.data.user.UserDao
import app.rootstock.data.user.UserRepository
import app.rootstock.data.user.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    fun provideTokenDao(appDatabase: AppDatabase): TokenDao {
        return appDatabase.tokenDao()
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        userDao: UserDao
    ): UserRepository {
        return UserRepositoryImpl(
            userDao = userDao,
        )
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("http://192.168.43.116:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()


    @Provides
    fun provideTokenRepository(tokenDao: TokenDao): TokenRepository {
        return TokenRepositoryImpl(tokenDao)
    }

    @Provides
    fun provideTokenInterceptor(tokenRepository: TokenRepository): TokenInterceptor {
        return TokenInterceptor(tokenRepository)
    }

    @Provides
    fun getClient(tokenInterceptor: TokenInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .callTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            // add JSON header interceptor
            .addInterceptor { chain ->
                val requestBuilder: Request.Builder = chain.request().newBuilder()
                requestBuilder.header("Content-Type", "application/json")
                chain.proceed(requestBuilder.build())
            }
            .addInterceptor(tokenInterceptor)
            .build()

}
