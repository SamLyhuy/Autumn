package kh.edu.rupp.ite.autumn.data.api.service

import kh.edu.rupp.ite.autumn.data.model.ApiResponse
import kh.edu.rupp.ite.autumn.data.model.Category
import kh.edu.rupp.ite.autumn.data.model.LogInResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("/api/category")
    suspend fun loadTest(): ApiResponse<List<Category>>

    @FormUrlEncoded
    @POST("login.php")
    suspend fun login(@Field("username") username: String, @Field("password") password: String): ApiResponse<LogInResponse>


    @GET("/api/category")
    suspend fun loadBooking(): ApiResponse<List<Category>>

}