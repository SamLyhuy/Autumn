package kh.edu.rupp.ite.autumn.data.model

import kh.edu.rupp.ite.autumn.global.AppConstants

data class ApiResponse<T>(
    val statusCode: Int,
    val message  : String,
    val token: String?,
    val data: T?

) {
    fun isSuccess(): Boolean {
        return message == AppConstants.API_STATUS_SUCCESS
    }
    fun isSuccessFetchTable(): Boolean {
        return message == AppConstants.API_STATUS_SUCCESS_TABLE
    }

    fun isSuccessCreateEvent(): Boolean {
        return message == AppConstants.API_STATUS_SUCCESS_CREATE_EVENT
    }

    fun isSuccessCreateFood(): Boolean {
        return message == AppConstants.API_STATUS_SUCCESS_CREATE_FOOD
    }


    fun isSuccessFetchFood(): Boolean {
        return message == AppConstants.API_STATUS_SUCCESS_FOOD
    }

    fun isSuccessBooking(): Boolean {
        return message == AppConstants.API_STATUS_SUCCESS_BOOKING
    }
    fun isSuccessSignUp(): Boolean {
        return message == AppConstants.API_STATUS_SUCCESS_SIGNUP
    }

}
