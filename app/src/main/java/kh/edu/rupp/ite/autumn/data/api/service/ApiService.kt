package kh.edu.rupp.ite.autumn.data.api.service

import kh.edu.rupp.ite.autumn.data.model.ApiResponse
import kh.edu.rupp.ite.autumn.data.model.ChatRequest
import kh.edu.rupp.ite.autumn.data.model.ChatResponse
import kh.edu.rupp.ite.autumn.data.model.EventData
import kh.edu.rupp.ite.autumn.data.model.FoodData
import kh.edu.rupp.ite.autumn.data.model.LogInResponse
import kh.edu.rupp.ite.autumn.data.model.PostEventRequest
import kh.edu.rupp.ite.autumn.data.model.PostFoodRequest
import kh.edu.rupp.ite.autumn.data.model.RegisterData
import kh.edu.rupp.ite.autumn.data.model.TableData
import kh.edu.rupp.ite.autumn.data.model.UserDataProfile
import kh.edu.rupp.ite.autumn.data.model.UserInfoResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("/event")
    suspend fun loadEvent(): ApiResponse<List<EventData>>

    @POST("/booking")
    suspend fun booking(@Header("Authorization") token: String,
                            @Body request: TableData): ApiResponse<TableData>

    @GET("/booking")
    suspend fun loadTable(@Query("date") date: String): ApiResponse<List<TableData>>

    @GET("/food")
    suspend fun loadfood(@Query("type") type: String): ApiResponse<List<FoodData>>

    @POST("/event")
    suspend fun postEvent(
        @Header("Authorization") token: String,
        @Body request: PostEventRequest
    ): ApiResponse<EventData>

    @POST("/user/info")
    suspend fun getUserInfo(@Header("Authorization") token: String): UserInfoResponse<UserDataProfile>

    @FormUrlEncoded
    @POST("/user/login")
    suspend fun login(@Field("email") email: String, @Field("password") password: String): LogInResponse

    @POST("/user")
    suspend fun register(@Body request: RegisterData): ApiResponse<RegisterData>

    @POST("/ai/chat")
    suspend fun chatAI(@Body request: ChatRequest): ApiResponse<List<FoodData>>

    @POST("/food")
    suspend fun postFood(
        @Header("Authorization") token: String,
        @Body request: PostFoodRequest
    ): ApiResponse<FoodData>

}