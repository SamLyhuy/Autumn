package kh.edu.rupp.ite.autumn.data.api.service

import kh.edu.rupp.ite.autumn.data.model.ApiResponse
import kh.edu.rupp.ite.autumn.data.model.Category
import kh.edu.rupp.ite.autumn.data.model.LogInResponse
import kh.edu.rupp.ite.autumn.data.model.LoginRequest
import kh.edu.rupp.ite.autumn.data.model.UserDataProfile
import kh.edu.rupp.ite.autumn.data.model.UserInfoResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @GET("/user/login")
    suspend fun loadTest(): ApiResponse<List<Category>>

    @GET("/user/login")
    suspend fun loadBooking(): ApiResponse<List<Category>>

    @POST("/user/info")
    suspend fun getUserInfo(@Header("Authorization") token: String): UserInfoResponse<UserDataProfile>

    @FormUrlEncoded
    @POST("/user/login")
    suspend fun login(@Field("email") email: String, @Field("password") password: String): LogInResponse




}