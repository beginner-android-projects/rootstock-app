package app.rootstock.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.rootstock.data.network.ResponseResult
import app.rootstock.data.result.Event
import app.rootstock.data.token.TokenRepository
import app.rootstock.data.user.UserRepository
import app.rootstock.ui.signup.AccountRepository
import app.rootstock.ui.workspace.WorkspaceRepository
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


interface SettingsItemClick {
    fun invoke()
}


@ActivityScoped
class SettingsViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val workspaceRepository: WorkspaceRepository,
) :
    ViewModel() {

    private val _event = MutableLiveData<Event<SettingsEvent>>()
    val event: LiveData<Event<SettingsEvent>> get() = _event

    fun logOut() {
        // when log out, revoke token and clear user login data
        viewModelScope.launch {
            userData.value?.userId?.let {
                val token = tokenRepository.getToken() ?: return@launch
                workspaceRepository.deleteAllWorkspacesLocal()
                tokenRepository.revokeToken(
                    token = token.refreshToken,
                    accessToken = token.accessToken
                )
                tokenRepository.removeToken()
                userRepository.deleteAll()
                _event.postValue(Event(SettingsEvent.LOG_OUT))
            }
        }
    }

    fun deleteAccount() {
    }

    val userData = userRepository.getUser()

}

enum class SettingsEvent {
    LOG_OUT
}