package kh.edu.rupp.ite.autumn.data.model

data class FoodData(
    val name: String,
    val price: Double,
    val type: String,
    val thumbnail: String,
    val isDeleted: Boolean? = null,
    val description: String? = null,
    val ingredients: List<String>? = null,
    val cuisine: String? = null,
    val spiciness: String? = null,
    val preparationTime: String? = null
)
