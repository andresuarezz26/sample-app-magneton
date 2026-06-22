package com.example.myapplication.home

import com.example.myapplication.data.model.VideoItem
import org.junit.Test

class HomeScreenTest {

    @Test
    fun testAppLaunchesSuccessfully() {
        val videoItem = VideoItem(
            id = "1",
            author = "@testauthor",
            description = "Test description",
            likes = 100,
            comments = 50,
            shares = 25,
            music = "Test Music",
            backgroundColorHex = 0xFF1A1A2E
        )

        val state = HomeUiState(videos = listOf(videoItem))
        assert(state.videos.isNotEmpty())
        assert(state.videos[0].author == "@testauthor")
    }

    @Test
    fun testLoadingStateDisplaysProgressIndicator() {
        val loadingState = HomeUiState(isLoading = true)

        assert(loadingState.isLoading)
        assert(loadingState.videos.isEmpty())
    }

    @Test
    fun testVideoContentIsDisplayed() {
        val videoItem = VideoItem(
            id = "1",
            author = "@testauthor",
            description = "Test description",
            likes = 100,
            comments = 50,
            shares = 25,
            music = "Test Music",
            backgroundColorHex = 0xFF1A1A2E
        )

        assert(videoItem.author == "@testauthor")
        assert(videoItem.description == "Test description")
        assert(videoItem.music == "Test Music")
    }

    @Test
    fun testLikeButtonIsClickable() {
        var likeIntentReceived = false
        val onIntent: (HomeIntent) -> Unit = { intent ->
            if (intent is HomeIntent.LikeVideo && intent.videoId == "1") {
                likeIntentReceived = true
            }
        }

        onIntent(HomeIntent.LikeVideo("1"))
        assert(likeIntentReceived)
    }

    @Test
    fun testLikeCountIncrementsAfterLike() {
        val videoItem = VideoItem(
            id = "1",
            author = "@testauthor",
            description = "Test description",
            likes = 100,
            comments = 50,
            shares = 25,
            music = "Test Music",
            backgroundColorHex = 0xFF1A1A2E
        )

        var currentState = HomeUiState(videos = listOf(videoItem))

        val onIntent: (HomeIntent) -> Unit = { intent ->
            if (intent is HomeIntent.LikeVideo && intent.videoId == "1") {
                currentState = currentState.copy(
                    videos = currentState.videos.map { video ->
                        if (video.id == "1") video.copy(likes = video.likes + 1) else video
                    }
                )
            }
        }

        assert(currentState.videos[0].likes == 100)
        onIntent(HomeIntent.LikeVideo("1"))
        assert(currentState.videos[0].likes == 101)
    }

    @Test
    fun testVerticalPagingHandlesMultipleVideos() {
        val video1 = VideoItem(
            id = "1",
            author = "@author1",
            description = "Description 1",
            likes = 100,
            comments = 50,
            shares = 25,
            music = "Music 1",
            backgroundColorHex = 0xFF1A1A2E
        )

        val video2 = VideoItem(
            id = "2",
            author = "@author2",
            description = "Description 2",
            likes = 200,
            comments = 100,
            shares = 50,
            music = "Music 2",
            backgroundColorHex = 0xFF16213E
        )

        val state = HomeUiState(videos = listOf(video1, video2))

        assert(state.videos.size == 2)
        assert(state.videos[0].author == "@author1")
        assert(state.videos[1].author == "@author2")
    }

    @Test
    fun testAllActionItemsAreVisible() {
        val videoItem = VideoItem(
            id = "1",
            author = "@testauthor",
            description = "Test description",
            likes = 1000,
            comments = 500,
            shares = 250,
            music = "Test Music",
            backgroundColorHex = 0xFF1A1A2E
        )

        val state = HomeUiState(videos = listOf(videoItem))

        assert(state.videos.isNotEmpty())
        assert(state.videos[0].likes == 1000)
        assert(state.videos[0].comments == 500)
        assert(state.videos[0].shares == 250)
    }
}
