package app.rootstock.ui.messages

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import app.rootstock.data.messages.Message
import app.rootstock.databinding.ItemMessageBinding


class MessageViewHolder(
    private val binding: ItemMessageBinding,
    private val lifecycleOwner: LifecycleOwner,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(message: Message?) {
        binding.lifecycleOwner = lifecycleOwner
        message?.let {
            binding.message = it
        }
        binding.executePendingBindings()
    }

}
