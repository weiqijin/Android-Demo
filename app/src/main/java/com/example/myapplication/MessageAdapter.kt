package com.example.myapplication



import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ui.theme.ChatMessage
import androidx.core.graphics.toColorInt
import com.example.myapplication.databinding.ItemChatBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(private val currentUser: String) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()

    inner class MessageViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            binding.tvMessageContent.text = message.content
            binding.tvMessageTime.text = formatTime(message.timestamp)

            if (message.from == currentUser) {
                // 自己发送的消息，靠右显示
                binding.messageCard.setCardBackgroundColor(
                    binding.root.resources.getColor(android.R.color.holo_blue_light, null)
                )
                binding.tvSenderName.text = "我"
                binding.tvSenderName.visibility = View.VISIBLE
            } else {
                // 对方发送的消息，靠左显示
                binding.messageCard.setCardBackgroundColor(
                    binding.root.resources.getColor(android.R.color.holo_green_light, null)
                )
                binding.tvSenderName.text = message.from
                binding.tvSenderName.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    private fun formatTime(timestamp: Long): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    }
}