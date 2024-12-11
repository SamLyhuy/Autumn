package kh.edu.rupp.ite.autumn.data.api.service

import kh.edu.rupp.ite.autumn.data.model.ApiResponse
import kh.edu.rupp.ite.autumn.data.model.Test
import retrofit2.http.GET

interface ApiService {

    @GET("posts")
    suspend fun loadTest(): ApiResponse<Test>
}