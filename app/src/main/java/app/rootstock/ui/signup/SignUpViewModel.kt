package app.rootstock.ui.signup

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.rootstock.data.result.Event
import app.rootstock.data.token.Token
import app.rootstock.data.user.UserRepository
import app.rootstock.data.user.UserWithPassword
import app.rootstock.ui.login.EventUserLogIn
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class EventUserSignUp { SUCCESS, USER_EXISTS, INVALID_DATA, FAILED, LOADING }

class SignUpViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val accountRepository: AccountRepository
) :
    ViewModel() {

    val user = MutableLiveData<SignUpUser>()

    private val _signUpStatus = MutableLiveData<Event<EventUserSignUp>>()
    val signUpStatus: LiveData<Event<EventUserSignUp>> get() = _signUpStatus

    val loading: LiveData<Boolean> = map(signUpStatus) {
        signUpStatus.value?.peekContent() == EventUserSignUp.LOADING
    }

    init {
        user.value = SignUpUser.build()
    }

    fun signUp() {
        // return if loading
        if (_signUpStatus.value?.peekContent() == EventUserSignUp.LOADING) return

        if (user.value?.allValid == false) {
            _signUpStatus.value = Event(EventUserSignUp.INVALID_DATA)
            return
        }

        user.value?.let {

            _signUpStatus.value = Event(EventUserSignUp.LOADING)

            registerUser(it)
        }
    }

    private fun registerUser(user: SignUpUser) {
        viewModelScope.launch {
            val response = runCatching {
                accountRepository.register(user)
            }

            response.getOrNull()?.let { res ->
                if (res.isSuccessful) {
                    res.body()?.let { userResponse ->
                        userRepository.insertUser(userResponse)
                        // if everything is ok, authenticate user to get tokens
                        authenticate(user)
                    }
                    return@launch
                }
                val event = when (res.code()) {
                    400 -> EventUserSignUp.USER_EXISTS
                    422 -> EventUserSignUp.INVALID_DATA
                    else -> EventUserSignUp.FAILED
                }
                _signUpStatus.postValue(Event(event))
            }
        }

    }

    private suspend fun authenticate(user: UserWithPassword) {
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
                    _signUpStatus.postValue(Event(EventUserSignUp.SUCCESS))
                }
                return
            }

            when (res.code()) {
                400, 422 -> _signUpStatus.postValue(Event(EventUserSignUp.INVALID_DATA))
                else -> _signUpStatus.postValue(Event(EventUserSignUp.FAILED))
            }
        }
    }

    fun stopSignUp() {
        viewModelScope.cancel()
    }


}