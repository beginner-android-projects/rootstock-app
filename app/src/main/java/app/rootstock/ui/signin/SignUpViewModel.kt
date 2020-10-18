package app.rootstock.ui.signin

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.rootstock.data.user.UserRepository
import dagger.hilt.android.scopes.ActivityScoped

enum class Event { SUCCESS, FAILED }


@ActivityScoped
class SignUpViewModel @ViewModelInject constructor(private val repository: UserRepository) :
    ViewModel() {

    val user = MutableLiveData<SignInUser>()

    private val _event = MutableLiveData<Event>()
    val event: LiveData<Event> get() = _event

    init {
        user.value = SignInUser.build()
    }

    fun signUp() {
        user.value?.let {
            if (!it.isEmailValid()) {
                _event.value = Event.FAILED
                return
            }
            // retrofit sign up
            _event.postValue(Event.FAILED)
        }
    }


}