package app.rootstock.ui.main

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import app.rootstock.adapters.WorkspaceEventHandler
import app.rootstock.data.channel.Channel
import app.rootstock.data.network.ResponseResult
import app.rootstock.data.prefs.CacheClass
import app.rootstock.data.prefs.SharedPrefsController
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
    object NoUser : WorkspaceEvent()
    object Error : WorkspaceEvent()
    object NavigateToRoot : WorkspaceEvent()
    class Backdrop(val close: Boolean) : WorkspaceEvent()
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
    private val spController: SharedPrefsController,
) :
    ViewModel(), WorkspaceEventHandler {

    private val _workspace = MutableLiveData<WorkspaceWithChildren>()

    private val _isAtRoot = MutableLiveData<Boolean>()
    val isAtRoot: LiveData<Boolean> get() = _isAtRoot

    var hasSwiped: Boolean = false

    private val _favouriteShowed = MutableLiveData(false)
    val favouriteShowed: LiveData<Boolean> get() = _favouriteShowed

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
                    _eventWorkspace.postValue(Event(WorkspaceEvent.NoUser))
                    return@launch
                }

            }
            id?.let { wsId ->
                val update = spController.shouldUpdateCache(CacheClass.Workspace(wsId))
                workspaceRepository.getWorkspace(wsId, update)
                    .collect {
                        when (it) {
                            is ResponseResult.Success -> {
                                it.data ?: return@collect
                                _workspace.value =
                                    it.data.apply { children.sortedBy { child -> child.createdAt } }
                                _channels.value = it.data.channels.toMutableList().apply {
                                    sortBy { channel -> channel.lastUpdate }
                                }.asReversed()
                                spController.updateCacheSettings(CacheClass.Workspace(wsId), false)
                            }
                            is ResponseResult.Error -> {
                                _eventWorkspace.postValue(Event(WorkspaceEvent.Error))
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

    fun changeFab(position: Int) {
        hasSwiped = true
        _pagerPosition.value = position
    }

    fun navigateToRoot() {
        _eventWorkspace.value = Event(WorkspaceEvent.NavigateToRoot)
    }

    fun updateChannel(channel: Channel) {
        if (isChannelValid(channel)) {
            // if new data is valid, update locally and send request to the server
            _channels.value = _channels.value?.apply {
                val oldChannel = find { channel.channelId == it.channelId }
                // return if there were no changes
                if (oldChannel == channel) return
                // otherwise replace old channel with new one
                oldChannel?.let { c ->
                    this[indexOf(c)] = channel
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
            val wsId = workspace.value?.workspaceId ?: return@launch
            when (channelRepository.deleteChannel(channelId, wsId).first()) {
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
        _isAtRoot.value = atRoot
    }

    fun addChannel(channel: Channel) {
        _channels.value = _channels.value?.apply {
            add(0, channel)
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
                }
                is ResponseResult.Error -> {
                }
            }
        }
    }

    fun resetPager() {
        hasSwiped = false
        _pagerPosition.value = 0
    }

    fun showFavourite() {
        _favouriteShowed.value = true
    }

    fun toggleBackdrop(close: Boolean) {
        _eventWorkspace.value = Event(WorkspaceEvent.Backdrop(close))
    }


}