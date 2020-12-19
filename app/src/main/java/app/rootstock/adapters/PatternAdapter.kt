package app.rootstock.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import app.rootstock.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class PatternAdapter constructor(
    private val items: MutableList<String>,
    private val onColorClicked: ((position: Int?, image: String?) -> Unit)
) : RecyclerView.Adapter<PatternAdapter.PatternViewHolder>() {

    var previousPickedPosition: Int? = null

    inner class PatternViewHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: String, position: Int) {
            itemView.findViewById<ImageView>(R.id.color_item)
                ?.let {
                    Glide.with(itemView)
                        .load(item)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .circleCrop()
                        .error(R.drawable.circle_channel)
                        .into(it)
                    it.setOnClickListener {
                        onColorClicked(position, item)
                        previousPickedPosition = position
                    }
                }
            if (position == 0) itemView.findViewById<ImageView>(R.id.color_item)
                ?.performClick()
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
