package app.rootstock.ui.settings

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.rootstock.data.user.UserRepository
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ActivityScoped
class SettingsViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
) :
    ViewModel() {

    val userData = userRepository.getUser()


}