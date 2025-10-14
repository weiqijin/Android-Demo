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
import com.example.myapplication.ui.theme.Message
import androidx.core.graphics.toColorInt


class MessageAdapter(private val messages: List<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView = itemView.findViewById<TextView>(R.id.text_message)
        val messageCardView = itemView.findViewById<CardView>(R.id.card_message)
    }

    override fun getItemCount() = messages.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.messageTextView.text = message.content
        val params = holder.messageCardView.layoutParams as FrameLayout.LayoutParams
        params.gravity = if (message.isSent) Gravity.END else Gravity.START
        holder.messageCardView.setCardBackgroundColor(if (message.isSent) "#E3F2FD".toColorInt() else Color.WHITE)
    }

}