package app.rootstock.ui.messages

import android.annotation.SuppressLint
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.*
import app.rootstock.api.SendMessage
import app.rootstock.data.channel.Channel
import app.rootstock.data.channel.ChannelI
import app.rootstock.data.messages.Message
import app.rootstock.data.messages.MessageRepository
import app.rootstock.data.network.CreateOperation
import app.rootstock.data.network.ResponseResult
import app.rootstock.data.result.Event
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

enum class MessageEvent {
    SUCCESS, ERROR, CREATED
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

        _messageEvent.value = (Event(MessageEvent.CREATED))
        viewModelScope.launch {

            when (val message = repository.sendMessage(message = sendMessage).first()) {
                is ResponseResult.Success -> {
                    _messageEvent.value = Event(MessageEvent.CREATED)
                    _messageEvent.postValue(Event(MessageEvent.SUCCESS))
                }
                is ResponseResult.Error -> {
                    _messageEvent.postValue(Event(MessageEvent.ERROR))
                }
            }
        }
    }
}