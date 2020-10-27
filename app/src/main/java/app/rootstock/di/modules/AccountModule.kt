package app.rootstock.di.modules

import app.rootstock.api.UserLogInService
import app.rootstock.api.UserSignUpService
import app.rootstock.ui.login.LogInLoader
import app.rootstock.ui.signup.AccountRepository
import app.rootstock.ui.signup.SignUpLoader
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
        signUpLoader: SignUpLoader,
        logInLoader: LogInLoader
    ): AccountRepository {
        return AccountRepository(signUpLoader, logInLoader)
    }

    @Provides
    fun provideSignUpLoader(signUpService: UserSignUpService): SignUpLoader {
        return SignUpLoader(signUpService)
    }

    @Provides
    fun provideUserSignUp(retrofit: Retrofit): UserSignUpService {
        return retrofit.create(UserSignUpService::class.java)
    }

    @Provides
    fun provideUserLogInService(retrofit: Retrofit): UserLogInService {
        return retrofit.create(UserLogInService::class.java)
    }
}