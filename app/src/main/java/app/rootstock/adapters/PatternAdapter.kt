package app.rootstock.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.rootstock.R
import app.rootstock.views.ChannelPickImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class PatternAdapter constructor(
    private val items: MutableList<String>,
    private val patternClicked: ((position: Int, image: String?) -> Unit),
    // for create dialog we want to preselect color and be sure, that
    // there is always an image attached to entity.
    private val selectFirst: Boolean = false
) : RecyclerView.Adapter<PatternAdapter.PatternViewHolder>() {

    var previousPickedPosition: Int? = null

    inner class PatternViewHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: String, position: Int) {
            itemView.findViewById<ChannelPickImageView>(R.id.color_item)
                ?.let {
                    Glide.with(it)
                        .load(item)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .circleCrop()
                        .placeholder(R.drawable.circle_channel)
                        .error(R.drawable.circle_channel)
                        .into(it)
                    it.setOnClickListener {
                        if (previousPickedPosition == position && selectFirst) return@setOnClickListener
                        else if (previousPickedPosition == position) {
                            patternClicked(position, null)
                            return@setOnClickListener
                        }
                        patternClicked(position, item)
                        previousPickedPosition = position
                    }
                    // preselect first element
                    if (selectFirst && position == 0) {
                        it.togglePicked()
                        patternClicked(position, item)
                        previousPickedPosition = position
                    }
                }
        }
    }

    fun updateList(newData: List<String>) {
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatternViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_color_channel, parent, false)
        return PatternViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatternViewHolder, position: Int) {
        val item = items.getOrNull(position) ?: return
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = items.size
}
