package kh.edu.rupp.ite.autumn.data.api.client

import com.google.gson.Gson
import kh.edu.rupp.ite.autumn.data.api.service.ApiService
import kh.edu.rupp.ite.autumn.global.AppConstants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient private constructor(){

    private val retrofit = Retrofit.Builder()
                .baseUrl(AppConstants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

    val apiService = retrofit.create(ApiService::class.java)

    companion object {

        private var instance: ApiClient? = null

        fun get(): ApiClient{
            if (instance == null) {
                instance = ApiClient()
            }

            return instance!!



        }

    }
}