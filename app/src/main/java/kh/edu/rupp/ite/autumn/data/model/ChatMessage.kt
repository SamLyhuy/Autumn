package kh.edu.rupp.ite.autumn.data.model

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val imageUrl: String? = null
)