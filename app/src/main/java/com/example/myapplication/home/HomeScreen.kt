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
    val displayedVideos = state.displayedVideos
    val pagerState = rememberPagerState(pageCount = { displayedVideos.size })
    val showEmptyFollowing = state.selectedTab == FeedTab.FOLLOWING && displayedVideos.isEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }
            showEmptyFollowing -> {
                EmptyFollowingState(modifier = Modifier.align(Alignment.Center))
            }
            else -> {
                VerticalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    if (page < displayedVideos.size) {
                        val video = displayedVideos[page]
                        VideoPage(
                            video = video,
                            isFollowed = video.author in state.followedAuthors,
                            onLike = { onIntent(HomeIntent.LikeVideo(video.id)) },
                            onToggleFollow = { onIntent(HomeIntent.ToggleFollow(video.author)) }
                        )
                    }
                }
            }
        }

        FeedTabsRow(
            selectedTab = state.selectedTab,
            onSelectTab = { onIntent(HomeIntent.SelectTab(it)) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp)
        )
    }
}

@Composable
private fun FeedTabsRow(
    selectedTab: FeedTab,
    onSelectTab: (FeedTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FeedTabLabel(
            text = "Following",
            isSelected = selectedTab == FeedTab.FOLLOWING,
            onClick = { onSelectTab(FeedTab.FOLLOWING) }
        )
        FeedTabLabel(
            text = "For You",
            isSelected = selectedTab == FeedTab.FOR_YOU,
            onClick = { onSelectTab(FeedTab.FOR_YOU) }
        )
    }
}

@Composable
private fun FeedTabLabel(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        fontSize = 15.sp,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun EmptyFollowingState(modifier: Modifier = Modifier) {
    Text(
        text = "Follow researchers to see their papers here",
        color = Color.White.copy(alpha = 0.8f),
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(horizontal = 32.dp)
    )
}

@Composable
private fun VideoPage(
    video: VideoItem,
    isFollowed: Boolean,
    onLike: () -> Unit,
    onToggleFollow: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(video.backgroundColorHex))
    ) {
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

            FollowToggle(isFollowed = isFollowed, onClick = onToggleFollow)

            ActionItem(icon = "❤️", count = formatCount(video.likes), onClick = onLike)
            ActionItem(icon = "💬", count = formatCount(video.comments), onClick = {})
            ActionItem(icon = "➡️", count = formatCount(video.shares), onClick = {})

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
private fun FollowToggle(isFollowed: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isFollowed) Color.White.copy(alpha = 0.15f) else Color(0xFFFE2C55))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = if (isFollowed) "Following" else "Follow",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ActionItem(icon: String, count: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(text = icon, fontSize = 30.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = count, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
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

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun HomeScreenEmptyFollowingPreview() {
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
                ),
                selectedTab = FeedTab.FOLLOWING
            ),
            onIntent = {}
        )
    }
}
