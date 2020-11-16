package app.rootstock.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.rootstock.data.channel.Channel
import app.rootstock.databinding.ItemChannelBinding

class ChannelListAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val editDialog: (v: View, c: Channel) -> Unit,
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
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Channel) {
            binding.channels = item
            binding.lifecycleOwner = lifecycleOwner
            binding.executePendingBindings()
            binding.channelColor.setColorFilter(Color.parseColor(item.backgroundColor))
            binding.channelEdit.setOnClickListener { editDialog(it, item) }
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

