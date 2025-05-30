package kh.edu.rupp.ite.autumn.data.api.client

import kh.edu.rupp.ite.autumn.data.api.service.ApiService
import kh.edu.rupp.ite.autumn.global.AppConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient private constructor(){

    private val logInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        //.addInterceptor(logInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)   // connection phase
        .readTimeout(120, TimeUnit.SECONDS)     // server may take up to 2m to send
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
                .baseUrl(AppConstants.API_BASE_URL)
                .client(httpClient)
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