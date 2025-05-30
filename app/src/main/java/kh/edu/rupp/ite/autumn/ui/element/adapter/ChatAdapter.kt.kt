package kh.edu.rupp.ite.autumn.ui.element.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kh.edu.rupp.ite.autumn.data.model.ChatMessage
import kh.edu.rupp.ite.autumn.data.model.FoodData
import kh.edu.rupp.ite.autumn.databinding.ItemBotMessageBinding
import kh.edu.rupp.ite.autumn.databinding.ItemBotTypingBinding
import kh.edu.rupp.ite.autumn.databinding.ItemChatFoodListBinding
import kh.edu.rupp.ite.autumn.databinding.ItemUserMessageBinding

class ChatAdapter(
    private val items: List<ChatMessage>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        const val TYPE_USER      = 0
        const val TYPE_BOT_TEXT  = 1
        const val TYPE_TYPING    = 2
        const val TYPE_FOOD_LIST = 3
    }

    override fun getItemViewType(position: Int): Int {
        val msg = items[position]
        return when {
            msg.isUser           -> TYPE_USER
            msg.isTyping         -> TYPE_TYPING
            msg.foodList != null -> TYPE_FOOD_LIST
            else                 -> TYPE_BOT_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when(viewType) {
        TYPE_USER -> {
            val binding = ItemUserMessageBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            UserViewHolder(binding)
        }
        TYPE_BOT_TEXT -> {
            val binding = ItemBotMessageBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            BotViewHolder(binding)
        }
        TYPE_TYPING -> {
            val binding = ItemBotTypingBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            TypingViewHolder(binding)
        }
        TYPE_FOOD_LIST -> {
            val binding = ItemChatFoodListBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            FoodListViewHolder(binding)
        }
        else -> throw IllegalArgumentException("Unknown viewType $viewType")
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = items[position]
        when (holder) {
            is UserViewHolder      -> holder.bind(msg)
            is BotViewHolder       -> holder.bind(msg)
            is TypingViewHolder    -> holder.bind()
            is FoodListViewHolder  -> holder.bind(msg.foodList!!)
        }
    }

    class UserViewHolder(
        private val binding: ItemUserMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: ChatMessage) {
            binding.userMessageText.text = msg.text
        }
    }

    class BotViewHolder(
        private val binding: ItemBotMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: ChatMessage) {
            binding.botMessageText.text = msg.text
        }
    }

    class TypingViewHolder(
        private val binding: ItemBotTypingBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.lottieTyping.apply {
                playAnimation()
                scaleX = 2f
                scaleY = 2f
            }
        }
    }

    class FoodListViewHolder(
        private val binding: ItemChatFoodListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(list: List<FoodData>) {
            binding.foodListRecyclerView.apply {
                layoutManager = LinearLayoutManager(
                    itemView.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = FoodAdapter().apply { setData(list) }
            }
        }
    }
}
