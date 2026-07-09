package com.example.myapplication.data.datasource

import com.example.myapplication.data.model.VideoItem
import com.example.myapplication.data.repository.VideoRepository

class RemoteVideoDataSource(private val repository: VideoRepository) {
    suspend fun getFeedVideos(): List<VideoItem> {
        return repository.getFeedVideos()
    }
}
