package app.rootstock.ui.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import app.rootstock.data.network.ResponseResult
import app.rootstock.data.result.Event
import app.rootstock.data.token.Token
import app.rootstock.data.user.UserRepository
import app.rootstock.data.user.UserWithPassword
import app.rootstock.ui.signup.AccountRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class EventUserLogIn { SUCCESS, INVALID_DATA, FAILED, LOADING }


class LogInViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val accountRepository: AccountRepository
) :
    ViewModel() {

    val user = MutableLiveData<LogInUser>()

    private val _logInStatus = MutableLiveData<Event<EventUserLogIn>>()
    val logInStatus: LiveData<Event<EventUserLogIn>> get() = _logInStatus

    val loading: LiveData<Boolean> = Transformations.map(logInStatus) {
        logInStatus.value?.peekContent() == EventUserLogIn.LOADING
    }

    init {
        user.value = LogInUser.build()
    }

    fun logIn() {
        // return if loading
        if (_logInStatus.value?.peekContent() == EventUserLogIn.LOADING) return

        if (user.value?.allValid != true) {
            _logInStatus.value = Event(EventUserLogIn.INVALID_DATA)
            return
        }

        user.value?.let {
            _logInStatus.value = Event(EventUserLogIn.LOADING)
            authenticate(it)

        }
    }

    private fun authenticate(user: UserWithPassword) {
        viewModelScope.launch {
            when (val token = accountRepository.authenticate(user).first()) {
                is ResponseResult.Success -> {
                    if (token.data != null) {
                        // update local user
                        userRepository.insertToken(
                            Token(
                                accessToken = token.data.accessToken,
                                refreshToken = token.data.refreshToken,
                                tokenType = token.data.tokenType
                            )
                        )
                        updateUserLocal(token.data.accessToken)
                        _logInStatus.postValue(Event(EventUserLogIn.SUCCESS))
                    } else {
                        _logInStatus.postValue(Event(EventUserLogIn.FAILED))
                    }
                }
                is ResponseResult.Error -> {
                    _logInStatus.postValue(Event(EventUserLogIn.FAILED))
                }
            }
        }
    }


    private suspend fun updateUserLocal(token: String) {
        // fetch user from network
        when (val user = accountRepository.getUserRemote(token).first()) {
            is ResponseResult.Success -> {
                if (user.data != null) {
                    // update local user
                    userRepository.insertUser(user.data)
                    _logInStatus.postValue(Event(EventUserLogIn.SUCCESS))
                } else {
                    _logInStatus.postValue(Event(EventUserLogIn.FAILED))
                }
            }
            is ResponseResult.Error -> {
                _logInStatus.postValue(Event(EventUserLogIn.FAILED))
            }
        }
    }

    fun stopLogIn() {
        viewModelScope.cancel()
    }

}