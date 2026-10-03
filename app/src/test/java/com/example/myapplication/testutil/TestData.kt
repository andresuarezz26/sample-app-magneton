package com.example.myapplication.testutil

import com.example.myapplication.data.model.VideoItem

fun videoItem(id: String, likes: Int = 0): VideoItem = VideoItem(
    id = id,
    author = "@author_$id",
    description = "desc $id",
    likes = likes,
    comments = 0,
    shares = 0,
    music = "music $id",
    backgroundColorHex = 0xFF000000
)
