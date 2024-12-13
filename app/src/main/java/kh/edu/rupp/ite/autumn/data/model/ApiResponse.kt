package kh.edu.rupp.ite.autumn.data.model

import kh.edu.rupp.ite.autumn.global.AppConstants

data class ApiResponse<T>(
//    val status: String,
//    val message  : String,
//   val data: T?
    val id: Int,
    val title: String,
    val content: String,
    val author: String,
    val timestamp: String,
    val likes: Int,
    val comments: List<Comment>
) {
//    fun isSuccess(): Boolean {
//        return status == AppConstants.API_STATUS_SUCCESS
//    }

    fun isSuccess(): Boolean {
        // For example, consider a response successful if the 'id' is valid
        return id > 0
    }
}
