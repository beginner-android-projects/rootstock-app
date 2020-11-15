package app.rootstock.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.rootstock.data.channel.Channel
import app.rootstock.databinding.ItemChannelBinding

class ChannelListAdapter constructor(
    private val lifecycleOwner: LifecycleOwner,
//    private val channels: LiveData<List<Channel>>,
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
//        private val channels: LiveData<List<Channel>>,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Channel) {
//            binding.channels = channels
            binding.channels = item
            binding.lifecycleOwner = lifecycleOwner
            binding.executePendingBindings()
            binding.channelColor.setColorFilter(Color.parseColor(item.backgroundColor))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val binding = ItemChannelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ChannelViewHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.bind(getItem(position))

    }
}

