package kh.edu.rupp.ite.autumn.ui.element.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.autumn.R
import kh.edu.rupp.ite.autumn.data.model.ChatMessage

class ChatAdapter(
    private val items: List<ChatMessage>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_USER = 0
        private const val TYPE_BOT_TEXT = 1
        private const val TYPE_FOOD = 2
        private const val TAG = "ChatAdapter"
    }

    override fun getItemViewType(position: Int): Int {
        val message = items[position]
        return when {
            message.isUser -> TYPE_USER
            message.imageUrl != null -> TYPE_FOOD
            else -> TYPE_BOT_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_USER -> UserViewHolder(inflater.inflate(R.layout.item_user_message, parent, false))
            TYPE_FOOD -> FoodViewHolder(inflater.inflate(R.layout.item_food, parent, false))
            else -> BotViewHolder(inflater.inflate(R.layout.item_bot_message, parent, false))
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = items[position]
        when (holder) {
            is UserViewHolder -> holder.messageText.text = message.text
            is BotViewHolder -> holder.messageText.text = message.text
            is FoodViewHolder -> {
                Log.d(TAG, "Binding FoodViewHolder for position=$position thumbnail=${message.imageUrl}")
                // Split on " - $" to extract name and price
                val parts = message.text.split(" - $")
                val name = parts.getOrNull(0) ?: message.text
                val pricePart = parts.getOrNull(1) ?: ""
                holder.productName.text = name
                holder.productPrice.text = if (pricePart.isNotEmpty()) "$$pricePart" else ""
                Picasso.get()
                    .load(message.imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(holder.productImage)
            }
        }
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.userMessageText)
    }

    class BotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.botMessageText)
    }

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
    }
}