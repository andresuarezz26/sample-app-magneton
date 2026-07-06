package com.example.myapplication.home

import com.example.myapplication.data.model.VideoItem
import com.example.myapplication.domain.usecase.GetFeedUseCase
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
    fun `toggling follow adds author to followedAuthors and toggling again removes it`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val author = viewModel.state.value.videos.first().author

        viewModel.onIntent(HomeIntent.ToggleFollow(author))
        advanceUntilIdle()
        assertEquals(setOf(author), viewModel.state.value.followedAuthors)

        viewModel.onIntent(HomeIntent.ToggleFollow(author))
        advanceUntilIdle()
        assertEquals(emptySet<String>(), viewModel.state.value.followedAuthors)
    }

    @Test
    fun `Following tab shows only videos from followed authors`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val followedAuthor = viewModel.state.value.videos[1].author
        viewModel.onIntent(HomeIntent.ToggleFollow(followedAuthor))
        advanceUntilIdle()

        viewModel.onIntent(HomeIntent.SelectTab(FeedTab.FOLLOWING))

        val state = viewModel.state.value
        assertEquals(FeedTab.FOLLOWING, state.selectedTab)
        assertEquals(listOf(followedAuthor), state.displayedVideos.map { it.author })
    }

    @Test
    fun `Following tab with no followed authors yields an empty displayed list`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        viewModel.onIntent(HomeIntent.SelectTab(FeedTab.FOLLOWING))

        val state = viewModel.state.value
        assertEquals(FeedTab.FOLLOWING, state.selectedTab)
        assertEquals(emptyList<Any>(), state.displayedVideos)
    }
}
