package com.example.myapplication.home

import com.example.myapplication.data.model.VideoItem
import com.example.myapplication.domain.usecase.GetFeedUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
import kotlin.coroutines.CoroutineContext

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

    private fun TestScope.createViewModel(
        ioDispatcher: CoroutineContext = testDispatcher
    ) = HomeViewModel(GetFeedUseCase(ioDispatcher))

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
    fun `state starts empty and not loading before the initial load runs`() = runTest(testDispatcher) {
        // StandardTestDispatcher queues init's coroutine; nothing runs until the scheduler advances.
        val viewModel = createViewModel()

        assertEquals(HomeUiState(), viewModel.state.value)
    }

    @Test
    fun `loading the feed emits isLoading true before emitting the loaded feed`() = runTest(testDispatcher) {
        // A distinct dispatcher instance (same scheduler) forces withContext to actually
        // dispatch, so the load has an observable in-flight state.
        val ioDispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = createViewModel(ioDispatcher)

        // The collector is unconfined, so it resumes at every MutableStateFlow write and
        // no intermediate value is conflated away.
        val emissions = mutableListOf<HomeUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect { emissions.add(it) }
        }

        advanceUntilIdle()

        assertEquals(listOf(false, true, false), emissions.map { it.isLoading })
        assertTrue(emissions.first().videos.isEmpty())
        assertTrue(emissions.last().videos.isNotEmpty())
    }

    @Test
    fun `initial load exposes the feed from the use case in order`() = runTest(testDispatcher) {
        val expected = GetFeedUseCase(testDispatcher).invoke()

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(expected, viewModel.state.value.videos)
    }

    @Test
    fun `LoadFeed intent reloads the feed and clears loading`() = runTest(testDispatcher) {
        val expected = GetFeedUseCase(testDispatcher).invoke()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(HomeIntent.LoadFeed)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(expected, state.videos)
        assertFalse(state.isLoading)
    }

    @Test
    fun `reloading the feed discards local like increments`() = runTest(testDispatcher) {
        // Pins current behaviour: the use case is the source of truth, so a reload
        // overwrites optimistic local likes. Documented, not necessarily desired.
        val viewModel = createViewModel()
        advanceUntilIdle()

        val targetId = "1"
        val originalLikes = viewModel.state.value.videos.first { it.id == targetId }.likes

        viewModel.onIntent(HomeIntent.LikeVideo(targetId))
        assertEquals(originalLikes + 1, viewModel.state.value.videos.first { it.id == targetId }.likes)

        viewModel.onIntent(HomeIntent.LoadFeed)
        advanceUntilIdle()

        assertEquals(originalLikes, viewModel.state.value.videos.first { it.id == targetId }.likes)
    }

    @Test
    fun `liking the same video twice increments likes by 2`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val targetId = "2"
        val originalLikes = viewModel.state.value.videos.first { it.id == targetId }.likes

        viewModel.onIntent(HomeIntent.LikeVideo(targetId))
        viewModel.onIntent(HomeIntent.LikeVideo(targetId))

        assertEquals(originalLikes + 2, viewModel.state.value.videos.first { it.id == targetId }.likes)
    }

    @Test
    fun `liking two different videos increments each independently`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val firstId = "1"
        val secondId = "3"
        val videosBefore = viewModel.state.value.videos.toList()

        viewModel.onIntent(HomeIntent.LikeVideo(firstId))
        viewModel.onIntent(HomeIntent.LikeVideo(secondId))

        val videosAfter = viewModel.state.value.videos
        videosBefore.forEach { original ->
            val updated = videosAfter.first { it.id == original.id }
            val expectedLikes = when (original.id) {
                firstId, secondId -> original.likes + 1
                else -> original.likes
            }
            assertEquals(expectedLikes, updated.likes)
        }
    }

    @Test
    fun `liking a video before the feed loads is a no-op`() = runTest(testDispatcher) {
        val expected = GetFeedUseCase(testDispatcher).invoke()

        val viewModel = createViewModel()
        viewModel.onIntent(HomeIntent.LikeVideo("1"))

        assertTrue(viewModel.state.value.videos.isEmpty())

        advanceUntilIdle()

        // The like landed on an empty list, so it is lost once the feed arrives.
        assertEquals(expected, viewModel.state.value.videos)
    }

    @Test
    fun `liking a video preserves list size and order`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val idsBefore = viewModel.state.value.videos.map { it.id }

        viewModel.onIntent(HomeIntent.LikeVideo("4"))

        assertEquals(idsBefore, viewModel.state.value.videos.map { it.id })
    }

    @Test
    fun `liking a video leaves the other fields of that video unchanged`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val targetId = "5"
        val before: VideoItem = viewModel.state.value.videos.first { it.id == targetId }

        viewModel.onIntent(HomeIntent.LikeVideo(targetId))

        val after = viewModel.state.value.videos.first { it.id == targetId }
        assertEquals(before.copy(likes = before.likes + 1), after)
    }
}
