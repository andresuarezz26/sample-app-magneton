package com.example.myapplication.data.repository

import com.example.myapplication.data.model.VideoItem
import com.example.myapplication.data.remote.VideoApiService

class VideoRepository(private val apiService: VideoApiService) {
    suspend fun getFeedVideos(): List<VideoItem> {
        return apiService.getFeedVideos()
    }
}
