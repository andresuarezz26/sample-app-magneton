package com.example.myapplication.home

import com.example.myapplication.data.model.FeedTab
import com.example.myapplication.data.model.VideoItem
import com.example.myapplication.domain.usecase.FollowUserUseCase
import com.example.myapplication.domain.usecase.GetFeedUseCase
import com.example.myapplication.domain.usecase.GetFollowingFeedUseCase
import com.example.myapplication.domain.usecase.UnfollowUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load populates feed with 6 videos and isLoading is false`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(6, state.videos.size)
        assertFalse(state.isLoading)
    }

    @Test
    fun `liking a video increments only that video likes by 1`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val targetId = "1"
        val videosBefore = viewModel.state.value.videos.toList()
        val initialLikes = videosBefore.first { it.id == targetId }.likes

        viewModel.onIntent(HomeIntent.LikeVideo(targetId))

        val videosAfter = viewModel.state.value.videos
        assertEquals(initialLikes + 1, videosAfter.first { it.id == targetId }.likes)
        videosAfter.filter { it.id != targetId }.forEach { video ->
            val original = videosBefore.first { it.id == video.id }
            assertEquals(original.likes, video.likes)
        }
    }

    @Test
    fun `liking a video with unknown id leaves feed unchanged`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val videosBefore = viewModel.state.value.videos.toList()

        viewModel.onIntent(HomeIntent.LikeVideo("unknown-id-999"))

        assertEquals(videosBefore, viewModel.state.value.videos)
    }

    @Test
    fun `followUser adds userId to followedUserIds`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val followUserUseCase = FollowUserUseCase(testDispatcher)
        val getFollowingFeedUseCase = GetFollowingFeedUseCase(testDispatcher, getFeedUseCase)
        val viewModel = HomeViewModel(getFeedUseCase, getFollowingFeedUseCase, followUserUseCase, UnfollowUserUseCase(testDispatcher))
        advanceUntilIdle()

        val initialFollowedCount = viewModel.state.value.followedUserIds.size
        viewModel.onIntent(HomeIntent.FollowUser("user_1"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(initialFollowedCount + 1, state.followedUserIds.size)
        assertTrue(state.followedUserIds.contains("user_1"))
    }

    @Test
    fun `unfollowUser removes userId from followedUserIds`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val followUserUseCase = FollowUserUseCase(testDispatcher)
        val unfollowUserUseCase = UnfollowUserUseCase(testDispatcher)
        val getFollowingFeedUseCase = GetFollowingFeedUseCase(testDispatcher, getFeedUseCase)
        val viewModel = HomeViewModel(getFeedUseCase, getFollowingFeedUseCase, followUserUseCase, unfollowUserUseCase)
        advanceUntilIdle()

        viewModel.onIntent(HomeIntent.FollowUser("user_1"))
        advanceUntilIdle()
        assertTrue(viewModel.state.value.followedUserIds.contains("user_1"))

        viewModel.onIntent(HomeIntent.UnfollowUser("user_1"))
        advanceUntilIdle()

        assertFalse(viewModel.state.value.followedUserIds.contains("user_1"))
    }

    @Test
    fun `switchFeedTab changes currentFeedTab`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        assertEquals(FeedTab.ForYou, viewModel.state.value.currentFeedTab)

        viewModel.onIntent(HomeIntent.SwitchFeedTab(FeedTab.Following))
        advanceUntilIdle()

        assertEquals(FeedTab.Following, viewModel.state.value.currentFeedTab)

        viewModel.onIntent(HomeIntent.SwitchFeedTab(FeedTab.ForYou))
        advanceUntilIdle()

        assertEquals(FeedTab.ForYou, viewModel.state.value.currentFeedTab)
    }

    @Test
    fun `getFollowingFeed filters videos by followedUserIds`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val getFollowingFeedUseCase = GetFollowingFeedUseCase(testDispatcher, getFeedUseCase)
        val followUserUseCase = FollowUserUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase, getFollowingFeedUseCase, followUserUseCase, UnfollowUserUseCase(testDispatcher))
        advanceUntilIdle()

        viewModel.onIntent(HomeIntent.FollowUser("user_1"))
        viewModel.onIntent(HomeIntent.FollowUser("user_2"))
        advanceUntilIdle()

        viewModel.onIntent(HomeIntent.SwitchFeedTab(FeedTab.Following))
        advanceUntilIdle()

        val followingVideos = viewModel.state.value.videos
        assertEquals(2, followingVideos.size)
        assertTrue(followingVideos.all { it.authorId in setOf("user_1", "user_2") })
    }

    @Test
    fun `showProfileModal sets selectedProfileUserId`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        viewModel.onIntent(HomeIntent.ShowProfileModal("user_1"))

        assertEquals("user_1", viewModel.state.value.selectedProfileUserId)
    }

    @Test
    fun `hideProfileModal clears selectedProfileUserId`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        viewModel.onIntent(HomeIntent.ShowProfileModal("user_1"))
        assertEquals("user_1", viewModel.state.value.selectedProfileUserId)

        viewModel.onIntent(HomeIntent.HideProfileModal)

        assertEquals(null, viewModel.state.value.selectedProfileUserId)
    }
}
