package app.rootstock.ui.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import app.rootstock.data.result.Event
import app.rootstock.data.token.Token
import app.rootstock.data.user.UserRepository
import app.rootstock.data.user.UserWithPassword
import app.rootstock.ui.signup.AccountRepository
import kotlinx.coroutines.cancel
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

        user.value?.let {

            _logInStatus.value = Event(EventUserLogIn.LOADING)

            authenticate(it)
        }
    }

    private fun authenticate(user: UserWithPassword) {
        viewModelScope.launch {
            val response = runCatching {
                accountRepository.authenticate(user)
            }
            response.getOrNull()?.let { res ->
                if (res.isSuccessful) {
                    res.body()?.let { tokenResponse ->
                        userRepository.insertToken(
                            Token(
                                accessToken = tokenResponse.accessToken,
                                refreshToken = tokenResponse.refreshToken,
                                tokenType = tokenResponse.tokenType
                            )
                        )
                        _logInStatus.postValue(Event(EventUserLogIn.SUCCESS))
                    }
                    return@launch
                }

                when (res.code()) {
                    400, 422 -> _logInStatus.postValue(Event(EventUserLogIn.INVALID_DATA))
                    else -> _logInStatus.postValue(Event(EventUserLogIn.FAILED))
                }
            }
        }
    }

    fun stopLogIn() {
        viewModelScope.cancel()
    }

}