package kh.edu.rupp.ite.autumn.data.model

data class Profile(
    val name: String,
    val email: String,
    val phonenumber: String,
    val role: String,
    val profile: String? = null,
    val booking_history: List<TableDataUser>
){
    fun email_phone_number(): String = "$email | $phonenumber"
}

data class TableDataUser(
    val date: String,
    val tables: List<String>
)
