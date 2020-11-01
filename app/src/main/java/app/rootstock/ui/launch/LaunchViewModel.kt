package app.rootstock.ui.launch

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import app.rootstock.data.result.Event
import app.rootstock.data.user.UserRepository
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ActivityScoped

class LaunchViewModel @ViewModelInject constructor(repository: UserRepository) :
    ViewModel() {

    val user = repository.getUser()

    val launchDestination = map(user) {
        if (it == null) {
            Event(LaunchDestination.SIGN_UP_ACTIVITY)
        } else {
            Event(LaunchDestination.WORKSPACE_ACTIVITY)
        }
    }

}

enum class LaunchDestination {
    WORKSPACE_ACTIVITY, SIGN_UP_ACTIVITY
}