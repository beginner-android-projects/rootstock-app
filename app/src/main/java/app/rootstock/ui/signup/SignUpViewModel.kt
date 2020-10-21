package app.rootstock.ui.signup

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.lifecycle.Transformations.map
import app.rootstock.data.result.Event
import app.rootstock.data.user.UserRepository
import kotlinx.coroutines.launch

enum class EventUserSignUp { SUCCESS, USER_EXISTS, INVALID_DATA, FAILED, LOADING }

class SignUpViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val registerRepository: RegisterRepository
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
        user.value?.let {
            if (!it.isDataValid()) {
                _signUpStatus.value = Event(EventUserSignUp.INVALID_DATA)
                return
            }

            _signUpStatus.value = Event(EventUserSignUp.LOADING)

            viewModelScope.launch {
                val response = runCatching {
                    registerRepository.register(it)
                }

                response.getOrNull() ?: let {
                    _signUpStatus.postValue(Event(EventUserSignUp.SUCCESS))
                    return@launch
                }

                when (response.getOrNull()?.code()) {
                    400 -> _signUpStatus.postValue(Event(EventUserSignUp.USER_EXISTS))
                    422 -> _signUpStatus.postValue(Event(EventUserSignUp.INVALID_DATA))
                    else -> _signUpStatus.postValue(Event(EventUserSignUp.FAILED))
                }
            }
        }
    }


}