package app.rootstock.ui.messages

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.rootstock.R
import app.rootstock.data.messages.Message
import java.text.Format
import java.text.SimpleDateFormat


class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val content: TextView = view.findViewById(R.id.content)
    private val date: TextView = view.findViewById(R.id.date)

    private var message: Message? = null

    fun bind(message: Message?) {
        if (message == null) {
            val resources = itemView.resources
            content.text = "IT iS NULLLLLL"
        } else {
            showRepoData(message)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showRepoData(message: Message) {
        this.message = message
        content.text = message.content
        date.text =
            message.createdAt.day.toString() + "/" + message.createdAt.hours.toString() + "/" + message.createdAt.minutes.toString()
    }

    companion object {
        fun create(parent: ViewGroup): MessageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message, parent, false)
            return MessageViewHolder(view)
        }
    }
}
