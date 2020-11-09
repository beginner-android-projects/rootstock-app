package app.rootstock.ui.main

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import app.rootstock.adapters.WorkspaceEventHandler
import app.rootstock.data.channel.Channel
import app.rootstock.data.network.ResponseResult
import app.rootstock.data.result.Event
import app.rootstock.data.user.UserRepository
import app.rootstock.data.workspace.Workspace
import app.rootstock.data.workspace.WorkspaceWithChildren
import app.rootstock.ui.workspace.WorkspaceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


sealed class WorkspaceEvent {
    class OpenWorkspace(val workspaceId: String) : WorkspaceEvent()
    class NoUser() : WorkspaceEvent()
    class Error() : WorkspaceEvent()
}

@ExperimentalCoroutinesApi
class WorkspaceViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val workspaceRepository: WorkspaceRepository
) :
    ViewModel(), WorkspaceEventHandler {

    private val _workspace = MutableLiveData<WorkspaceWithChildren>()

    val workspace: LiveData<WorkspaceWithChildren>
        get() = _workspace

    val workspacesChildren: LiveData<List<Workspace>> = _workspace.map { workspaceWithChildren ->
        workspaceWithChildren ?: return@map listOf()
        workspaceWithChildren.children
    }

    val channels: LiveData<List<Channel>> = _workspace.map { workspaceWithChildren ->
        workspaceWithChildren ?: return@map listOf()
        workspaceWithChildren.channels
    }

    private val _eventWorkspace = MutableLiveData<Event<WorkspaceEvent>>()
    val eventWorkspace: LiveData<Event<WorkspaceEvent>> get() = _eventWorkspace


    fun loadWorkspace(workspaceId: String?) {
        _workspace.value = null
        var id = workspaceId
        viewModelScope.launch {
            if (workspaceId == null) {
                id = userRepository.getUserId()

                if (id == null) {
                    _eventWorkspace.postValue(Event(WorkspaceEvent.NoUser()))
                    return@launch
                }

            }
            id?.let { wsId ->
                workspaceRepository.getWorkspace(wsId)
                    .collect {
                        when (it) {
                            is ResponseResult.Success -> {
                                _workspace.value =
                                    it.data?.apply { children.sortedBy { child -> child.createdAt } }
                                        ?.apply { channels.sortedBy { channel -> channel.lastUpdate } }
                            }
                            is ResponseResult.Error -> {
                                _eventWorkspace.postValue(Event(WorkspaceEvent.Error()))
                            }
                        }
                    }
            }
        }
    }


    override fun workspaceClicked(workspaceId: String) {
        // set to null to avoid blinking workspaces
        _workspace.value = null
        _eventWorkspace.value = Event(WorkspaceEvent.OpenWorkspace(workspaceId))
    }

}