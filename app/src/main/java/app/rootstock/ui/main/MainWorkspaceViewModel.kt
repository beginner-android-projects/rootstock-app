package app.rootstock.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.rootstock.data.network.ResponseResult
import app.rootstock.data.result.Event
import app.rootstock.data.user.UserRepository
import app.rootstock.data.workspace.WorkspaceWithChildren
import app.rootstock.ui.workspace.WorkspaceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

enum class MainWorkspaceEvent {
    NO_USER, ERROR
}

@ExperimentalCoroutinesApi
class MainWorkspaceViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val workspaceRepository: WorkspaceRepository
) :
    ViewModel() {

    private val _workspace = MutableLiveData<WorkspaceWithChildren>()
    val workspace: LiveData<WorkspaceWithChildren> get() = _workspace

    private val _eventWorkspace = MutableLiveData<Event<MainWorkspaceEvent>>()
    val eventWorkspace: LiveData<Event<MainWorkspaceEvent>> get() = _eventWorkspace


    init {
        viewModelScope.launch {
            val userId = userRepository.getUserId()

            if (userId == null) {
                _eventWorkspace.postValue(Event(MainWorkspaceEvent.NO_USER))
                return@launch
            }

            workspaceRepository.getMainWorkspace(userId)
                .collect {
                    when (it) {
                        is ResponseResult.Success -> {
                            _workspace.value = it.data
                        }
                        is ResponseResult.Error -> {
                            _eventWorkspace.postValue(Event(MainWorkspaceEvent.ERROR))
                        }
                    }
                }
        }
    }

}