package kh.edu.rupp.ite.autumn.data.api.service

import kh.edu.rupp.ite.autumn.data.model.ApiResponse
import kh.edu.rupp.ite.autumn.data.model.Comment
import kh.edu.rupp.ite.autumn.data.model.Test
import retrofit2.http.GET

interface ApiService {

    @GET("/api/type/1")
    suspend fun loadTest(): ApiResponse<List<Comment>>
}