package com.example.chatapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.JoinItemBinding
import com.example.chatapp.databinding.MessageFromItemBinding
import com.example.chatapp.databinding.MessageToItemBinding
import com.example.chatapp.model.Message
import com.example.chatapp.model.OnItemLongClickListener
import com.example.chatapp.utils.*
import java.util.*

private const val TAG = "MessagesAdapter"

class MessagesAdapter(val storageManager: StorageManager) :
    ListAdapter<Message, RecyclerView.ViewHolder>(MESSAGE_COMPARATOR) {

    private lateinit var longClickListener: OnItemLongClickListener<Message.TextMessage>

    companion object {
        val MESSAGE_COMPARATOR = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return (oldItem is Message.TextMessage && newItem is Message.TextMessage &&
                        oldItem.message == newItem.message) ||
                        (oldItem is Message.LogMessage && newItem is Message.LogMessage &&
                                oldItem.type == newItem.type)
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem == newItem
        }
        const val RECEIVER_TYPE = 0
        const val SENDER_TYPE = 1
        const val LOG_TYPE = 2
    }

    inner class ReceiverViewHolder(private val binding: MessageFromItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message.TextMessage) {
            binding.apply {
                val date: String = when {
                    isToday(message.createdAt) -> {
                        "Today"
                    }
                    isYesterday(message.createdAt) -> {
                        "Yesterday"
                    }
                    else -> {
                        message.createdAt.getDate(Constants.DATE_PATTERN)
                    }
                }
                val calender = Calendar.getInstance()
                calender.timeInMillis = message.createdAt
                val mDate = calender.get(Calendar.DAY_OF_MONTH)
                setDateVisibility(bindingAdapterPosition, textGchatDateOther, mDate, calender)
                textGchatDateOther.text = date
                textGchatUserOther.text = message.sender
                textGchatMessageOther.text = message.message
                textGchatTimestampOther.text = message.createdAt.getDate()
            }
        }
    }

    inner class SenderViewHolder(private val binding: MessageToItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val message = getItem(position)
                    message?.let {
                        it as Message.TextMessage
                        longClickListener.onItemLongClickListener(it)
                        true
                    } ?: false
                } else {
                    false
                }
            }
        }

        fun bind(message: Message.TextMessage) {
            binding.apply {
                binding.apply {
                    val date: String = when {
                        isToday(message.createdAt) -> {
                            "Today"
                        }
                        isYesterday(message.createdAt) -> {
                            "Yesterday"
                        }
                        else -> {
                            message.createdAt.getDate(Constants.DATE_PATTERN)
                        }
                    }
                    val calender = Calendar.getInstance()
                    calender.timeInMillis = message.createdAt
                    val mDate = calender.get(Calendar.DAY_OF_MONTH)
                    setDateVisibility(
                        bindingAdapterPosition,
                        textGchatDateMe,
                        mDate,
                        calender
                    )
                    textGchatMessageMe.setLinkTextColor(Color.WHITE)
                    textGchatDateMe.text = date
                    textGchatMessageMe.text = message.message
                    textGchatTimestampMe.text = message.createdAt.getDate()
                }
            }
        }
    }

    inner class JoinViewHolder(private val binding: JoinItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(join: Message.LogMessage) {
            binding.apply {
                joinedTv.text = join.content
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            RECEIVER_TYPE -> ReceiverViewHolder(
                MessageFromItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            SENDER_TYPE -> SenderViewHolder(
                MessageToItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            LOG_TYPE -> JoinViewHolder(
                JoinItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw Exception("No viewtype")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        message?.let {
            when (it) {
                is Message.TextMessage -> {
                    val viewType = getItemViewType(position)
                    if (viewType == RECEIVER_TYPE) {
                        (holder as ReceiverViewHolder).bind(it)
                    } else {
                        (holder as SenderViewHolder).bind(it)
                    }
                }
                is Message.LogMessage -> {
                    if (it.type == "join" || it.type == "leave") {
                        (holder as JoinViewHolder).bind(it)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Message.TextMessage -> {
                if ((getItem(position) as Message.TextMessage).sender == storageManager.getFullname()) {
                    SENDER_TYPE
                } else {
                    RECEIVER_TYPE
                }
            }
            else -> {
                LOG_TYPE
            }
        }
    }

    private fun setDateVisibility(
        bindingAdapterPosition: Int,
        dateTv: TextView,
        mDate: Int,
        calender: Calendar,
    ) {
        if (bindingAdapterPosition > 0) {
            if (getItem(bindingAdapterPosition - 1) !is Message.TextMessage
                && bindingAdapterPosition - 1 == 0
            ) {
                dateTv.visibility = View.VISIBLE
            } else {
                var position = bindingAdapterPosition
                do {
                    position -= 1
                } while (getItem(position) !is Message.TextMessage)
                val previousMessage =
                    getItem(position) as Message.TextMessage
                calender.timeInMillis = previousMessage.createdAt
                val prevDate = calender.get(Calendar.DAY_OF_MONTH)
                if (mDate > prevDate) {
                    dateTv.visibility = View.VISIBLE
                } else {
                    dateTv.visibility = View.GONE
                }
            }
        } else {
            dateTv.visibility = View.VISIBLE
        }
    }

    fun setListener(listener: OnItemLongClickListener<Message.TextMessage>) {
        this.longClickListener = listener
    }
}