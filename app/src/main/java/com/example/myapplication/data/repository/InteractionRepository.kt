package com.example.myapplication.data.repository

import kotlinx.coroutines.delay

class InteractionRepository {

    suspend fun like(paperId: String): Boolean {
        delay(200)
        return true
    }

    suspend fun bookmark(paperId: String): Boolean {
        delay(200)
        return true
    }

    suspend fun share(paperId: String): Boolean {
        delay(200)
        return true
    }
}
