package app.rootstock.ui.signup

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.rootstock.data.result.Event
import app.rootstock.data.user.UserRepository
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
                        _signUpStatus.postValue(Event(EventUserSignUp.SUCCESS))
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

    fun stopSignUp() {
        viewModelScope.cancel()
    }


}