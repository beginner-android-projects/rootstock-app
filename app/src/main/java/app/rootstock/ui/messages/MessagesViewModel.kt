package app.rootstock.ui.messages

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.rootstock.api.EditMessage
import app.rootstock.api.SendMessage
import app.rootstock.data.channel.Channel
import app.rootstock.data.messages.Message
import app.rootstock.data.messages.MessageRepository
import app.rootstock.data.network.ResponseResult
import app.rootstock.data.result.Event
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class MessageEvent {
    ERROR, CREATED, DELETED
}

@ActivityScoped
@OptIn(ExperimentalPagingApi::class)
class MessagesViewModel @ViewModelInject constructor(private val repository: MessageRepository) :
    ViewModel() {

    private val _messageEvent = MutableLiveData<Event<MessageEvent>>()
    val messageEvent: LiveData<Event<MessageEvent>> get() = _messageEvent

    private val _channel = MutableLiveData<Channel>()

    val channel: LiveData<Channel>
        get() = _channel

    fun setChannel(channel: Channel) {
        _channel.value = channel
    }

    private var currentQueryValue: Long? = null

    private var currentSearchResult: Flow<PagingData<Message>>? = null

    fun searchRepo(channelId: Long, refresh: Boolean = false): Flow<PagingData<Message>> {
        val lastResult = currentSearchResult
        if (channelId == currentQueryValue && lastResult != null && !refresh) {
            return lastResult
        }
        currentQueryValue = channelId
        val newResult: Flow<PagingData<Message>> = repository.getSearchResultStream(channelId)
            .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }

    fun sendMessage(content: String?) {
        content ?: return
        val id = _channel.value?.channelId ?: return

        val sendMessage = SendMessage(content = content, channelId = id)

        viewModelScope.launch {

            when (val message = repository.sendMessage(message = sendMessage).first()) {
                is ResponseResult.Success -> {
                    _messageEvent.value = Event(MessageEvent.CREATED)
                }
                is ResponseResult.Error -> {
                    _messageEvent.postValue(Event(MessageEvent.ERROR))
                }
            }
        }
    }

    fun editMessage(id: Long, content: String) {
        val editMessage = EditMessage(content = content)
        viewModelScope.launch {
            when (val response = repository.editMessage(editMessage, id).first()) {
                is ResponseResult.Success -> {
                    _messageEvent.value = Event(MessageEvent.CREATED)
                }
                is ResponseResult.Error -> {
                    _messageEvent.postValue(Event(MessageEvent.ERROR))
                }
            }
        }
    }

    fun deleteMessage(messageId: Long) {
        viewModelScope.launch {
            when (val response = repository.deleteMessage(messageId).first()) {
                is ResponseResult.Success -> {
                    _messageEvent.value = Event(MessageEvent.DELETED)
                }
                is ResponseResult.Error -> {
                    _messageEvent.postValue(Event(MessageEvent.ERROR))
                }
            }
        }
    }
}