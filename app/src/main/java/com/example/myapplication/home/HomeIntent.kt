package com.example.myapplication.home

import com.example.myapplication.data.model.FeedTab

sealed interface HomeIntent {
    data object LoadFeed : HomeIntent
    data class LikeVideo(val videoId: String) : HomeIntent
    data class SwitchFeedTab(val tab: FeedTab) : HomeIntent
    data class FollowUser(val userId: String) : HomeIntent
    data class UnfollowUser(val userId: String) : HomeIntent
    data class ShowProfileModal(val userId: String) : HomeIntent
    data object HideProfileModal : HomeIntent
}
