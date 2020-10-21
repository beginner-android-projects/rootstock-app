package app.rootstock.di.modules


import android.content.Context
import app.rootstock.api.UserSignUpService
import app.rootstock.data.db.AppDatabase
import app.rootstock.data.network.LiveDataCallAdapterFactory
import app.rootstock.data.user.UserDao
import app.rootstock.data.user.UserRepository
import app.rootstock.ui.signup.RegisterRepository
import app.rootstock.ui.signup.SignUpLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    @Singleton
    @Provides
    fun provideRegisterRepository(signUpLoader: SignUpLoader): RegisterRepository {
        return RegisterRepository(signUpLoader)
    }

    @Provides
    fun provideSignUpLoader(signUpService: UserSignUpService): SignUpLoader {
        return SignUpLoader(signUpService)
    }

    @Singleton
    @Provides
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepository(userDao = userDao)
    }

//    @Singleton
//    @Provides
//    fun provideRetrofit(): Retrofit =
//        Retrofit.Builder()
//            .baseUrl("http://0.0.0.0:8080/")
//            .build()

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("http://192.168.43.116:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(client)
            .build()

    @Provides
    fun getClient(): OkHttpClient = OkHttpClient()

    @Singleton
    @Provides
    fun provideUserSignUp(retrofit: Retrofit): UserSignUpService {
        return retrofit.create(UserSignUpService::class.java)
    }
}
