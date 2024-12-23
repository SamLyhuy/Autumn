package kh.edu.rupp.ite.autumn.data.model

import kh.edu.rupp.ite.autumn.global.AppConstants

data class LogInResponse (
    val message: String,
    val token: String,
){
//    fun isSuccess(): Boolean {
//        return statusCodeLogIn == AppConstants.API_STATUS_SUCCESS_LOGIN
//    }

}

