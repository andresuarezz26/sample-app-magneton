package com.example.myapplication.home

import com.example.myapplication.data.model.VideoItem
import com.example.myapplication.domain.usecase.GetFeedUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

    private val getFeedUseCase = GetFeedUseCase(testDispatcher)

    private fun createViewModel() = HomeViewModel(getFeedUseCase)

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
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(6, state.videos.size)
        assertFalse(state.isLoading)
    }

    @Test
    fun `liking a video increments only that video likes by 1`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
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
        val viewModel = createViewModel()
        advanceUntilIdle()

        val videosBefore = viewModel.state.value.videos.toList()

        viewModel.onIntent(HomeIntent.LikeVideo("unknown-id-999"))

        assertEquals(videosBefore, viewModel.state.value.videos)
    }

    @Test
    fun `initial state is the default HomeUiState before the load coroutine runs`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            // No advanceUntilIdle on purpose: init's launch is still queued on the
            // StandardTestDispatcher, so nothing has been emitted yet.
            assertEquals(HomeUiState(), viewModel.state.value)

            advanceUntilIdle()
        }

    @Test
    fun `loading the feed emits isLoading true before delivering the videos`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val emissions = mutableListOf<HomeUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.state.toList(emissions)
            }

            advanceUntilIdle()

            assertEquals(listOf(false, true, false), emissions.map { it.isLoading })
            assertEquals(emptyList<VideoItem>(), emissions[1].videos)
            assertEquals(6, emissions.last().videos.size)
        }

    @Test
    fun `loaded feed matches the use case output field for field`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(getFeedUseCase(), viewModel.state.value.videos)
    }

    @Test
    fun `explicit LoadFeed intent reloads the feed and discards local like increments`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.onIntent(HomeIntent.LikeVideo("1"))

            viewModel.onIntent(HomeIntent.LoadFeed)
            advanceUntilIdle()

            assertEquals(getFeedUseCase(), viewModel.state.value.videos)
            assertFalse(viewModel.state.value.isLoading)
        }

    @Test
    fun `liking the same video twice increments its likes by 2`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()
        val before = viewModel.state.value.videos.first { it.id == "2" }.likes

        viewModel.onIntent(HomeIntent.LikeVideo("2"))
        viewModel.onIntent(HomeIntent.LikeVideo("2"))

        assertEquals(before + 2, viewModel.state.value.videos.first { it.id == "2" }.likes)
    }

    @Test
    fun `liking a video preserves list order and every other field`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()
        val before = viewModel.state.value.videos
        val target = before.first { it.id == "3" }

        viewModel.onIntent(HomeIntent.LikeVideo("3"))

        val after = viewModel.state.value.videos
        assertEquals(before.map { it.id }, after.map { it.id })
        assertEquals(target.copy(likes = target.likes + 1), after.first { it.id == "3" })
    }

    @Test
    fun `liking before the feed loads is a no-op and does not break the pending load`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()

            viewModel.onIntent(HomeIntent.LikeVideo("1"))
            assertEquals(emptyList<VideoItem>(), viewModel.state.value.videos)

            advanceUntilIdle()
            assertEquals(getFeedUseCase(), viewModel.state.value.videos)
        }

    @Test
    fun `liking an unknown id does not emit a new state`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val emissions = mutableListOf<HomeUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.toList(emissions)
        }
        advanceUntilIdle()
        val countAfterLoad = emissions.size

        viewModel.onIntent(HomeIntent.LikeVideo("unknown-id-999"))

        assertEquals(countAfterLoad, emissions.size)
    }
}
