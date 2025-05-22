package kh.edu.rupp.ite.autumn.data.model

data class ChatResponse(
    val message: String,
    val data: List<FoodData>?
)