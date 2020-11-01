package app.rootstock.di.modules

import app.rootstock.api.UserInfoService
import app.rootstock.api.UserLogInService
import app.rootstock.api.UserSignUpService
import app.rootstock.ui.signup.AccountRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import retrofit2.Retrofit

/**
 * Module for AccountActivity
 */
@InstallIn(ActivityComponent::class)
@Module
object AccountModule {

    @ActivityScoped
    @Provides
    fun provideAccountRepository(
        signUpLoader: UserSignUpService,
        logInLoader: UserLogInService,
        userInfoService: UserInfoService
    ): AccountRepository {
        return AccountRepository(signUpLoader, logInLoader, userInfoService)
    }

    @Provides
    fun provideUserSignUp(retrofit: Retrofit): UserSignUpService {
        return retrofit.create(UserSignUpService::class.java)
    }

    @Provides
    fun provideUserLogInService(retrofit: Retrofit): UserLogInService {
        return retrofit.create(UserLogInService::class.java)
    }

    @Provides
    fun provideUserInfoService(retrofit: Retrofit): UserInfoService {
        return retrofit.create(UserInfoService::class.java)
    }


}