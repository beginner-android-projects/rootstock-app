package app.rootstock.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import app.rootstock.adapters.WorkspaceEventHandler
import app.rootstock.data.channel.Channel
import app.rootstock.data.network.ResponseResult
import app.rootstock.data.result.Event
import app.rootstock.data.user.UserRepository
import app.rootstock.data.workspace.Workspace
import app.rootstock.data.workspace.WorkspaceWithChildren
import app.rootstock.ui.channels.ChannelRepository
import app.rootstock.ui.workspace.WorkspaceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


sealed class WorkspaceEvent {
    class OpenWorkspace(val workspaceId: String) : WorkspaceEvent()
    class NoUser() : WorkspaceEvent()
    class Error() : WorkspaceEvent()
    class NavigateToRoot() : WorkspaceEvent()
}

enum class EditEvent {
    EDIT_OPEN, EDIT_EXIT
}

enum class PagerEvent {
    PAGER_SCROLLED
}

@ExperimentalCoroutinesApi
class WorkspaceViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val workspaceRepository: WorkspaceRepository,
    private val channelRepository: ChannelRepository,
) :
    ViewModel(), WorkspaceEventHandler {

    private val _workspace = MutableLiveData<WorkspaceWithChildren>()

    var isAtRoot: Boolean? = null

    val workspace: LiveData<WorkspaceWithChildren>
        get() = _workspace

    val workspacesChildren: LiveData<List<Workspace>> = _workspace.map { workspaceWithChildren ->
        workspaceWithChildren ?: return@map listOf()
        workspaceWithChildren.children
    }

    val channels: LiveData<MutableList<Channel>>
        get() = _channels

    private val _channels: MutableLiveData<MutableList<Channel>> = MutableLiveData(mutableListOf())

    private val _eventWorkspace = MutableLiveData<Event<WorkspaceEvent>>()
    val eventWorkspace: LiveData<Event<WorkspaceEvent>> get() = _eventWorkspace

    private val _eventChannel = MutableLiveData<Event<EditEvent>>()
    val eventEdit: LiveData<Event<EditEvent>> get() = _eventChannel

    private val _pagerPosition = MutableLiveData<Int>()
    val pagerPosition: LiveData<Int> get() = _pagerPosition

    private val _pagerScrolled = MutableLiveData<Event<PagerEvent>>()
    val pagerScrolled: LiveData<Event<PagerEvent>> get() = _pagerScrolled

    fun editDialogOpened() {
        _eventChannel.value = Event(EditEvent.EDIT_OPEN)
    }

    fun editChannelStop() {
        _eventChannel.value = Event(EditEvent.EDIT_EXIT)
    }


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
                                it.data ?: return@collect
                                _workspace.value =
                                    it.data.apply { children.sortedBy { child -> child.createdAt } }
                                _channels.value = it.data.channels.toMutableList().apply {
                                    sortBy { channel -> channel.lastUpdate }
                                }
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
        pageScrolled()
        // set to null to avoid blinking workspaces
        _workspace.value = null
        _eventWorkspace.value = Event(WorkspaceEvent.OpenWorkspace(workspaceId))
    }

    fun animateFab(position: Int) {
        _pagerPosition.value = position
    }

    fun navigateToRoot() {
        _eventWorkspace.value = Event(WorkspaceEvent.NavigateToRoot())
    }

    fun updateChannel(channel: Channel) {
        if (isChannelValid(channel)) {
            // if new data is valid, update locally and send request to the server
            _channels.value = _channels.value?.apply {
                val oldChannel = find { channel.channelId == it.channelId }
                // return if there were no changes
                if (oldChannel?.equals(channel) == true) return
                oldChannel?.apply {
                    name = channel.name
                    backgroundColor = channel.backgroundColor
                }
            }
            viewModelScope.launch {
                updateChannelRemote(channel)
            }
        } else {
            // todo
            // notify
        }
    }


    private suspend fun updateChannelRemote(channelToUpdate: Channel) {
        // fetch user from network
        when (val channel = channelRepository.updateChannel(channelToUpdate).first()) {
            is ResponseResult.Success -> {

            }
            is ResponseResult.Error -> {
            }
            else -> {
            }
        }
    }


    private fun isChannelValid(channel: Channel): Boolean {
        if (channel.name.isBlank()) return false
        return true
    }

    fun deleteChannel(channelId: Long) {
        _channels.value = _channels.value?.apply {
            val c = find { it.channelId == channelId }
            remove(c)
        }
        viewModelScope.launch {
            when (channelRepository.deleteChannel(channelId).first()) {
                is ResponseResult.Success -> {
                }
                is ResponseResult.Error -> {
                }
            }
        }
    }

    fun pageScrolled() {
        _pagerScrolled.value = Event(PagerEvent.PAGER_SCROLLED)
    }

    fun setRoot(atRoot: Boolean) {
        isAtRoot = atRoot
    }

    fun addChannel(channel: Channel) {
        _channels.value = _channels.value?.apply {
            add(channel)
        }
    }

    fun deleteWorkspace(wsId: String) {
        _workspace.value = _workspace.value?.apply {
            children.apply {
                val w = find { it.workspaceId == wsId }
                remove(w)
            }
        }
        viewModelScope.launch {
            when (workspaceRepository.deleteWorkspace(wsId).first()) {
                is ResponseResult.Success -> {
//                    _eventChannel.value = Event(ChannelEvent.CHANNEL_DELETED)
                }
                is ResponseResult.Error -> {
//                    _eventChannel.value = Event(ChannelEvent.ERROR)
                }
            }
        }
    }


}