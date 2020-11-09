package app.rootstock.di.modules


import android.content.Context
import app.rootstock.data.db.AppDatabase
import app.rootstock.data.network.*
import app.rootstock.data.token.TokenDao
import app.rootstock.data.token.TokenRepository
import app.rootstock.data.token.TokenRepositoryImpl
import app.rootstock.data.token.TokenService
import app.rootstock.data.user.UserDao
import app.rootstock.data.user.UserRepository
import app.rootstock.data.user.UserRepositoryImpl
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class AppModule {

    companion object {
        const val SERVER_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
        const val API_BASE_URL = "http://192.168.43.116:8000"
    }

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
        return UserRepositoryImpl(userDao = userDao)
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        client: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .client(client)
            .build()
    }


    @Singleton
    @Provides
    fun provideReLogInObservable(): ReLogInObservable {
        return ReLogInObservableImpl()
    }

    /**
     * Provide Gson and specify date format to correctly convert date object from server's to [java.util.Date]
     */
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().setDateFormat(SERVER_DATE_PATTERN).create()

    @Singleton
    @Provides
    fun provideGsonConverter(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }


    @Singleton
    @Provides
    fun provideTokenRepository(
        tokenDao: TokenDao,
        tokenService: TokenService,
        userRepository: UserRepository
    ): TokenRepository {
        return TokenRepositoryImpl(tokenDao, tokenService, userRepository)
    }

    @Provides
    fun provideTokenRemote(gsonConverterFactory: GsonConverterFactory): TokenService {
        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(TokenService::class.java)
    }

    @Singleton
    @Provides
    fun provideTokenInterceptor(tokenRepository: TokenRepository): TokenInterceptor {
        return TokenInterceptor(tokenRepository)
    }

    @Singleton
    @Provides
    fun provideServerAuthenticator(
        tokenRepository: TokenRepository,
        tokenInterceptor: TokenInterceptor,
        reLogInObservable: ReLogInObservable
    ): ServerAuthenticator {
        return ServerAuthenticator(tokenRepository, tokenInterceptor, reLogInObservable)
    }

    @Singleton
    @Provides
    fun provideJsonInterceptor(): JsonInterceptor {
        return JsonInterceptor()
    }

    @Provides
    fun getClient(
        tokenInterceptor: TokenInterceptor,
        authenticator: ServerAuthenticator,
        jsonInterceptor: JsonInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            // add JSON header interceptor
            .addInterceptor(jsonInterceptor)
            .addInterceptor(tokenInterceptor)
            .authenticator(authenticator)
            .build()

}
