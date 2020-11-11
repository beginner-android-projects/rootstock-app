package app.rootstock.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.rootstock.data.channel.Channel
import app.rootstock.databinding.ItemChannelBinding

class ChannelListAdapter constructor(
    private val lifecycleOwner: LifecycleOwner,
    private val channels: LiveData<List<Channel>>,
) : ListAdapter<Channel, ChannelListAdapter.ChannelViewHolder>(
    object :
        DiffUtil.ItemCallback<Channel>() {

        override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem == newItem
        }

    }) {

    inner class ChannelViewHolder constructor(
        private val binding: ItemChannelBinding,
        private val lifecycleOwner: LifecycleOwner,
        private val channels: LiveData<List<Channel>>,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Channel, position: Int) {
            binding.channels = channels
            binding.positionIndex = position
            binding.lifecycleOwner = lifecycleOwner
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val binding = ItemChannelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ChannelViewHolder(binding, lifecycleOwner, channels)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.bind(getItem(position), position)

    }
}
