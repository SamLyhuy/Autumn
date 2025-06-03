package kh.edu.rupp.ite.autumn.data.model

data class PostFoodRequest(
    val name: String,
    val price: Double,
    val type: String,
    val thumbnail: String,

    // all of these are nullable defaults—Gson will skip them if they're null
    val isdeleted: Boolean?      = null,
    val description: String?     = null,
    val ingredients: List<String>? = null,
    val cuisine: String?         = null,
    val spiciness: String?       = null,
    val preparationTime: String? = null
)
