package com.example.myapplication.data.remote

import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://localhost:8080/"

    fun createApiService(): VideoApiService {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(MockInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()

        return retrofit.create(VideoApiService::class.java)
    }
}
