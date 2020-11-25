package app.rootstock.ui.messages

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.*
import app.rootstock.data.channel.ChannelI
import app.rootstock.data.messages.Message
import app.rootstock.data.messages.MessageRepository
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


@ActivityScoped
class MessagesViewModel @ViewModelInject constructor(private val repository: MessageRepository) :
    ViewModel() {

    val _channel = MutableLiveData<ChannelI>()

    val channel: LiveData<ChannelI>
        get() = _channel

    fun setChannel(channel: ChannelI) {
        _channel.value = channel
    }

    private var currentQueryValue: Long? = null

    private var currentSearchResult: Flow<PagingData<Message>>? = null

    @ExperimentalPagingApi
    fun searchRepo(channelId: Long): Flow<PagingData<Message>> {
        val lastResult = currentSearchResult
        if (channelId == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = channelId
        val newResult: Flow<PagingData<Message>> = repository.getSearchResultStream(channelId)
            .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }
}