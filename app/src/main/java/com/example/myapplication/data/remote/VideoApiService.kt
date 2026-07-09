package com.example.myapplication.data.remote

import com.example.myapplication.data.model.VideoItem
import retrofit2.http.GET

interface VideoApiService {
    @GET("/videos")
    suspend fun getFeedVideos(): List<VideoItem>
}
