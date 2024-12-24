package kh.edu.rupp.ite.autumn.data.model

data class Profile(
    val name: String,
    val email: String,
    val phonenumber: String,
    val role: String,
    val profile: String
){
    fun email_phone_number(): String = "$email | $phonenumber"
}

