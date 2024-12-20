package kh.edu.rupp.ite.autumn.data.model

data class Profile(
    val firstName: String,
    val lastName: String,
    val coverImage: String?,
    val profileImage: String?,
)
{
    fun fullname(): String  = "$firstName $lastName"
}
