package app.rootstock.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import app.rootstock.data.user.UserRepository
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
class SettingsViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
) :
    ViewModel() {

    val userData = userRepository.getUser()

}