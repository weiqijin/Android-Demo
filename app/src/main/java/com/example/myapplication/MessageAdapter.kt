package com.example.myapplication



import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
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

        val layoutSentMessage: LinearLayout = itemView.findViewById(R.id.layout_sent_message)
        val layoutReceivedMessage: LinearLayout = itemView.findViewById(R.id.layout_received_message)
        val tvSentMessage: TextView = itemView.findViewById(R.id.tvSentMessage)
        val tvSentTime: TextView = itemView.findViewById(R.id.tvSentTime)
        val tvReceivedMessage: TextView = itemView.findViewById(R.id.tvReceivedMessage)
        val tvReceivedTime: TextView = itemView.findViewById(R.id.tvReceivedTime)
        val tvSenderName: TextView = itemView.findViewById(R.id.tvSenderName)
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
        val message = messages[position]

        if (message.from == currentUser) {
            // 自己发送的消息 - 显示右侧布局
            holder.layoutSentMessage.visibility = View.VISIBLE
            holder.layoutReceivedMessage.visibility = View.GONE

            holder.tvSentMessage.text = message.content
            holder.tvSentTime.text = formatTime(message.timestamp)
        } else {
            // 接收的消息 - 显示左侧布局
            holder.layoutSentMessage.visibility = View.GONE
            holder.layoutReceivedMessage.visibility = View.VISIBLE

            holder.tvSenderName.text = message.from
            holder.tvReceivedMessage.text = message.content
            holder.tvReceivedTime.text = formatTime(message.timestamp)
        }
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