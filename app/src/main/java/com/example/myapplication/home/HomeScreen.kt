package com.example.myapplication.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.VideoItem
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeContent(state = state, onIntent = viewModel::onIntent)
}

@Composable
private fun HomeContent(state: HomeUiState, onIntent: (HomeIntent) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        } else {
            when (state.selectedTab) {
                Tab.Following, Tab.ForYou -> {
                    val pagerState = rememberPagerState(pageCount = { state.videos.size })
                    VerticalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        if (page < state.videos.size) {
                            val video = state.videos[page]
                            VideoPage(
                                video = video,
                                isBookmarked = state.savedVideoIds.contains(video.id),
                                onLike = { onIntent(HomeIntent.LikeVideo(video.id)) },
                                onRemoveBookmark = { onIntent(HomeIntent.RemoveBookmark(video.id)) },
                                onTabSelect = { tab -> onIntent(HomeIntent.SelectTab(tab)) },
                                selectedTab = state.selectedTab,
                                onBookmark = { onIntent(HomeIntent.SaveVideo(video.id)) }
                            )
                        }
                    }
                }
                Tab.Saved -> {
                    val savedVideos = state.videos.filter { state.savedVideoIds.contains(it.id) }
                    if (savedVideos.isEmpty()) {
                        EmptyState()
                    } else {
                        val pagerState = rememberPagerState(pageCount = { savedVideos.size })
                        VerticalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            if (page < savedVideos.size) {
                                val video = savedVideos[page]
                                VideoPage(
                                    video = video,
                                    isBookmarked = true,
                                    onLike = { onIntent(HomeIntent.LikeVideo(video.id)) },
                                    onRemoveBookmark = { onIntent(HomeIntent.RemoveBookmark(video.id)) },
                                    onTabSelect = { tab -> onIntent(HomeIntent.SelectTab(tab)) },
                                    selectedTab = state.selectedTab
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoPage(
    video: VideoItem,
    isBookmarked: Boolean,
    onLike: () -> Unit,
    onRemoveBookmark: () -> Unit,
    onTabSelect: (Tab) -> Unit,
    selectedTab: Tab,
    onBookmark: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(video.backgroundColorHex))
    ) {
        // Top nav: Following / For You / Saved tabs
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabButton(
                text = "Following",
                isSelected = selectedTab == Tab.Following,
                onClick = { onTabSelect(Tab.Following) }
            )
            TabButton(
                text = "For You",
                isSelected = selectedTab == Tab.ForYou,
                onClick = { onTabSelect(Tab.ForYou) }
            )
            TabButton(
                text = "Saved",
                isSelected = selectedTab == Tab.Saved,
                onClick = { onTabSelect(Tab.Saved) }
            )
        }

        // Right action rail
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Author avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = video.author.take(2).uppercase(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            ActionItem(icon = "❤️", count = formatCount(video.likes), onClick = onLike)
            ActionItem(icon = "💬", count = formatCount(video.comments), onClick = {})
            ActionItem(icon = "➡️", count = formatCount(video.shares), onClick = {})

            val bookmarkIcon = if (isBookmarked) "🔖" else "🔗"
            ActionItem(icon = bookmarkIcon, count = "", onClick = {
                if (isBookmarked) onRemoveBookmark() else onBookmark()
            })

            // Music disc
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "♪", color = Color.White, fontSize = 20.sp)
            }
        }

        // Bottom: author + description + music
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 80.dp, bottom = 20.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = video.author,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = video.description,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "♫  ${video.music}",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
        fontSize = 15.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = "📄",
                fontSize = 64.sp
            )
            Text(
                text = "No saved papers yet",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap the bookmark icon to save papers",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ActionItem(icon: String, count: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(text = icon, fontSize = 30.sp)
        if (count.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = count, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

private fun formatCount(count: Int): String = when {
    count >= 1_000_000 -> "${count / 1_000_000}M"
    count >= 1_000 -> "${count / 1_000}K"
    else -> count.toString()
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun HomeScreenPreview() {
    MyApplicationTheme {
        HomeContent(
            state = HomeUiState(
                videos = listOf(
                    VideoItem(
                        id = "1",
                        author = "@astro_facts",
                        description = "Did you know black holes emit Hawking radiation? The universe is wild!",
                        likes = 48200,
                        comments = 1203,
                        shares = 892,
                        music = "Cosmic Vibes — Science Beats",
                        backgroundColorHex = 0xFF1A1A2E
                    )
                )
            ),
            onIntent = {}
        )
    }
}
