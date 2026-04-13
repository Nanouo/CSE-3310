package com.reread.app.ui.messaging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.reread.app.R
import com.reread.app.data.Conversation

class ConversationAdapter(
    private val conversations: List<Conversation>,
    private val onClick: (Conversation) -> Unit
) : RecyclerView.Adapter<ConversationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conversation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(conversations[position])
    }

    override fun getItemCount() = conversations.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvBook: TextView        = itemView.findViewById(R.id.tv_conv_book)
        private val tvOtherUser: TextView   = itemView.findViewById(R.id.tv_conv_other_user)
        private val tvLastMessage: TextView = itemView.findViewById(R.id.tv_conv_last_message)

        fun bind(conv: Conversation) {
            tvBook.text        = conv.bookTitle
            tvOtherUser.text   = "with ${conv.sellerUsername}"
            tvLastMessage.text = if (conv.lastMessage.isBlank()) "No messages yet" else conv.lastMessage
            itemView.setOnClickListener { onClick(conv) }
        }
    }
}