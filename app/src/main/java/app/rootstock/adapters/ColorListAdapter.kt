package app.rootstock.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import app.rootstock.R

class ColorListAdapter constructor(
    private val items: List<String>,
    private val onColorClicked: ((view: View, position: Int?) -> Unit)
) : RecyclerView.Adapter<ColorListAdapter.ColorViewHolder>() {

    var previousPickedPosition: Int? = null

    inner class ColorViewHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: String, position: Int) {
            itemView.findViewById<ImageView>(R.id.color_item)
                ?.setColorFilter(Color.parseColor(item))
            itemView.findViewById<ImageView>(R.id.color_item)?.setOnClickListener {
                onColorClicked(it, position)
                previousPickedPosition = position
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_color_channel, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val item = items.getOrNull(position) ?: return
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = items.size
}
