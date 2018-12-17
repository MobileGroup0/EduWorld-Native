package group0.eduworld.messaging

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import group0.eduworld.R
import group0.eduworld.Util

private const val VIEW_TYPE_MESSAGE_SENT = 1
private const val VIEW_TYPE_MESSAGE_RECEIVED = 2

class ChatRecyclerViewAdapter(context: Context, messageList: List<Message>) : Adapter<RecyclerView.ViewHolder>() {
    private var mContext: Context = context
    private  var mMessageList: List<Message> = messageList


    override fun getItemCount(): Int {
        return mMessageList.size
    }

    // Determines the appropriate ViewType according to the sender of the message.
    override fun getItemViewType(position: Int): Int {
        return if(mMessageList[position] is ReceivedMessage) VIEW_TYPE_MESSAGE_RECEIVED
        else VIEW_TYPE_MESSAGE_SENT
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = mMessageList.get(position)

        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageViewHolder).bind(message as SentMessage)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageViewHolder).bind(message as ReceivedMessage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View

        return if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sent_message, parent, false)
            SentMessageViewHolder(view)
        } else {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_received_message, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    private class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageText: TextView = itemView.findViewById(R.id.text_message_body)
        var timeText : TextView = itemView.findViewById(R.id.text_message_time)

        fun bind(message: SentMessage) {
            messageText.text = message.text

            // Format the stored timestamp into a readable String using method.
            timeText.text = Util.formatDateTime(message.createdAt)
        }
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageText: TextView = itemView.findViewById(R.id.text_message_body)
        var timeText : TextView = itemView.findViewById(R.id.text_message_time)
        var nameText : TextView = itemView.findViewById(R.id.text_message_name)

        fun bind(message: ReceivedMessage) {
            messageText.text = message.text

            nameText.text = message.sender

            // Format the stored timestamp into a readable String using method.
            timeText.text = Util.formatDateTime(message.createdAt)
        }
    }
}