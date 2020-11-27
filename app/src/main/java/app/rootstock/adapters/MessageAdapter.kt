package app.rootstock.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.paging.PagingDataDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import app.rootstock.R
import app.rootstock.data.messages.Message
import app.rootstock.databinding.ItemChannelBinding
import app.rootstock.databinding.ItemMessageBinding
import app.rootstock.ui.messages.MessageViewHolder


/**
 * Adapter for the list of repositories.
 */
class MessageAdapter(private val lifecycleOwner: LifecycleOwner) :
    PagingDataAdapter<Message, MessageViewHolder>(MESSAGE_COMPARATOR) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MessageViewHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val repoItem = getItem(position)
        if (repoItem != null) {
            holder.bind(repoItem)
        }

    }

    companion object {
        private val MESSAGE_COMPARATOR = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.messageId == newItem.messageId
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem == newItem
        }
    }
}
