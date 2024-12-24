package kh.edu.rupp.ite.autumn.data.model

import kh.edu.rupp.ite.autumn.global.AppConstants

data class ApiResponse<T>(
    val statusCode: Int,
    val message  : String,
    val data: T?

) {
    fun isSuccess(): Boolean {
        return message == AppConstants.API_STATUS_SUCCESS
    }

}
