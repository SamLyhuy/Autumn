package kh.edu.rupp.ite.autumn.data.model

data class ChatMessage(
    val text: String? = null,
    val isUser: Boolean,
    val foodList: List<FoodData>? = null,
    val isTyping: Boolean        = false
)
