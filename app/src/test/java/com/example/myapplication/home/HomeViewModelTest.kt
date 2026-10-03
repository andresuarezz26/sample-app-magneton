package com.example.myapplication.home

import com.example.myapplication.data.model.VideoItem
import com.example.myapplication.domain.usecase.GetFeedUseCase
import com.example.myapplication.testutil.MainDispatcherRule
import com.example.myapplication.testutil.videoItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val feed = listOf(videoItem("a", likes = 10), videoItem("b", likes = 20))
    private val getFeedUseCase = mockk<GetFeedUseCase>()

    @Before
    fun setUp() {
        coEvery { getFeedUseCase() } returns feed
    }

    @Test
    fun `init dispatches LoadFeed and exposes loading state while the feed is in flight`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val gate = CompletableDeferred<List<VideoItem>>()
            coEvery { getFeedUseCase() } coAnswers { gate.await() }

            val viewModel = HomeViewModel(getFeedUseCase)
            runCurrent()

            assertTrue(viewModel.state.value.isLoading)
            assertTrue(viewModel.state.value.videos.isEmpty())

            gate.complete(feed)
            advanceUntilIdle()

            assertFalse(viewModel.state.value.isLoading)
            assertEquals(feed, viewModel.state.value.videos)
        }

    @Test
    fun `successful load populates videos and clears loading`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = HomeViewModel(getFeedUseCase)
            advanceUntilIdle()

            assertEquals(HomeUiState(videos = feed, isLoading = false), viewModel.state.value)
            coVerify(exactly = 1) { getFeedUseCase() }
        }

    @Test
    fun `LoadFeed intent re-fetches the feed`() = runTest(mainDispatcherRule.testDispatcher) {
        val refreshed = listOf(videoItem("c", likes = 5))
        coEvery { getFeedUseCase() } returnsMany listOf(feed, refreshed)

        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()
        assertEquals(feed, viewModel.state.value.videos)

        viewModel.onIntent(HomeIntent.LoadFeed)
        advanceUntilIdle()

        coVerify(exactly = 2) { getFeedUseCase() }
        assertEquals(refreshed, viewModel.state.value.videos)
    }

    @Test
    fun `LikeVideo increments only the target video by one`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = HomeViewModel(getFeedUseCase)
            advanceUntilIdle()

            viewModel.onIntent(HomeIntent.LikeVideo("a"))

            val videos = viewModel.state.value.videos
            assertEquals(11, videos.first { it.id == "a" }.likes)
            assertEquals(20, videos.first { it.id == "b" }.likes)
        }

    @Test
    fun `LikeVideo twice increments by two`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        viewModel.onIntent(HomeIntent.LikeVideo("a"))
        viewModel.onIntent(HomeIntent.LikeVideo("a"))

        assertEquals(12, viewModel.state.value.videos.first { it.id == "a" }.likes)
    }

    @Test
    fun `LikeVideo with unknown id leaves feed unchanged`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = HomeViewModel(getFeedUseCase)
            advanceUntilIdle()

            viewModel.onIntent(HomeIntent.LikeVideo("missing"))

            assertEquals(feed, viewModel.state.value.videos)
        }

    @Test
    fun `LikeVideo before the feed has loaded is a no-op`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = HomeViewModel(getFeedUseCase)

            viewModel.onIntent(HomeIntent.LikeVideo("a"))

            assertTrue(viewModel.state.value.videos.isEmpty())
        }
}
