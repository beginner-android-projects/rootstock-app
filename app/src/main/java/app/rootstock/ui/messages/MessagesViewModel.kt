package app.rootstock.ui.messages

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import app.rootstock.data.channel.ChannelI
import app.rootstock.data.messages.Message
import dagger.hilt.android.scopes.ActivityScoped


@ActivityScoped
class MessagesViewModel @ViewModelInject constructor() : ViewModel() {

    private val _channel = MutableLiveData<ChannelI>()

//    private val _messages = Transformations.map(_channel) {
//        getMessages(it.channelId)
//    }
//    val messages: LiveData<Message> get() = _messages


    fun setChannel(channel: ChannelI) {
        _channel.value = channel
    }

//    suspend fun getMessages(channelId: Long): Message {
//        TODO()
//    }

}