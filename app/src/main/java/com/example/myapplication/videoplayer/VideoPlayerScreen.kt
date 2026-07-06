package com.example.myapplication.videoplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.myapplication.data.model.VideoItem

@Composable
fun VideoPlayerScreen(
    videos: List<VideoItem> = emptyList(),
    initialIndex: Int = 0,
    onBack: () -> Unit = {},
    viewModel: VideoPlayerViewModel = remember {
        VideoPlayerViewModel(videos, initialIndex)
    }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
        }
    }

    LaunchedEffect(state.currentIndex, state.videos) {
        if (state.videos.isNotEmpty()) {
            val video = state.videos.getOrNull(state.currentIndex)
            if (video != null && video.videoUrl.isNotEmpty()) {
                exoPlayer.setMediaItem(MediaItem.fromUri(video.videoUrl))
                exoPlayer.prepare()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("playerScreen") // for Espresso testing
    ) {
        if (state.videos.isNotEmpty()) {
            val currentVideo = state.videos.getOrNull(state.currentIndex) ?: return@Box

            AndroidView(
                factory = { context ->
                    androidx.media3.ui.PlayerView(context).apply {
                        player = exoPlayer
                        useController = false
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("playerSurface")
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            when {
                                dragAmount < -50 -> {
                                    viewModel.onVideoIndexChanged(state.currentIndex + 1)
                                }
                                dragAmount > 50 -> {
                                    viewModel.onVideoIndexChanged(state.currentIndex - 1)
                                }
                            }
                        }
                    }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            if (dragAmount < -30 && !state.isBottomSheetExpanded) {
                                viewModel.onBottomSheetStateChanged(true)
                            }
                        }
                    }
            ) {
                Spacer(modifier = Modifier.weight(1f))

                PaperDetailsBottomSheet(
                    video = currentVideo,
                    isExpanded = state.isBottomSheetExpanded,
                    onLike = { viewModel.toggleLike(currentVideo.id) },
                    onBookmark = { viewModel.toggleBookmark(currentVideo.id) },
                    onShare = { viewModel.onShare(currentVideo.id) },
                    isLiked = state.likedVideos.contains(currentVideo.id),
                    isBookmarked = state.bookmarkedVideos.contains(currentVideo.id),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bottomPanel")
                        .pointerInput(Unit) {
                            detectVerticalDragGestures { _, dragAmount ->
                                if (dragAmount > 30 && state.isBottomSheetExpanded) {
                                    viewModel.onBottomSheetStateChanged(false)
                                }
                            }
                        }
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
                    .navigationBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentVideo.author.take(2).uppercase(),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
