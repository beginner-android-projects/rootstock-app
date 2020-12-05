package app.rootstock.ui.messages

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import app.rootstock.data.messages.Message
import app.rootstock.databinding.ItemMessageBinding


class MessageViewHolder(
    private val binding: ItemMessageBinding,
    private val lifecycleOwner: LifecycleOwner,
    private val delete: (messageId: Long, anchor: View) -> Unit,
    private val edit: (message: Message) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(message: Message?) {
        binding.lifecycleOwner = lifecycleOwner
        message?.let { m ->
            binding.message = m
            binding.messageContainer.setOnClickListener { delete(m.messageId, binding.root) }
            binding.edit.setOnClickListener { edit(m) }
        }
        binding.executePendingBindings()
    }

}
