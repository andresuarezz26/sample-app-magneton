package com.example.myapplication.domain.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FollowRepository {

    private val _followedAuthors = MutableStateFlow<Set<String>>(emptySet())
    val followedAuthors: StateFlow<Set<String>> = _followedAuthors.asStateFlow()

    fun toggleFollow(author: String) {
        _followedAuthors.update { current ->
            if (author in current) current - author else current + author
        }
    }
}
