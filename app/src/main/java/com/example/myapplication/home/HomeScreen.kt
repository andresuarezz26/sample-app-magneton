package com.example.myapplication.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.FeedTab
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
        } else if (state.currentFeedTab == FeedTab.Following && state.followedUserIds.isEmpty()) {
            FollowingEmptyState(onIntent = onIntent)
        } else if (state.videos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No videos available", color = Color.White)
            }
        } else {
            val pagerState = rememberPagerState(pageCount = { state.videos.size })
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                if (page < state.videos.size) {
                    val video = state.videos[page]
                    VideoPage(
                        video = video,
                        currentFeedTab = state.currentFeedTab,
                        isFollowing = video.authorId in state.followedUserIds,
                        onLike = { onIntent(HomeIntent.LikeVideo(video.id)) },
                        onSwitchTab = { tab -> onIntent(HomeIntent.SwitchFeedTab(tab)) },
                        onCreatorClick = { onIntent(HomeIntent.ShowProfileModal(video.authorId)) }
                    )
                }
            }
        }

        if (state.selectedProfileUserId != null) {
            ProfileModal(
                userId = state.selectedProfileUserId,
                isFollowing = state.selectedProfileUserId in state.followedUserIds,
                onClose = { onIntent(HomeIntent.HideProfileModal) },
                onFollow = { onIntent(HomeIntent.FollowUser(state.selectedProfileUserId)) },
                onUnfollow = { onIntent(HomeIntent.UnfollowUser(state.selectedProfileUserId)) }
            )
        }
    }
}

@Composable
private fun FollowingEmptyState(onIntent: (HomeIntent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No Following Yet",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Follow researchers to see their content here",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onIntent(HomeIntent.SwitchFeedTab(FeedTab.ForYou)) },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Explore Researchers")
        }
    }
}

@Composable
private fun VideoPage(
    video: VideoItem,
    currentFeedTab: FeedTab,
    isFollowing: Boolean,
    onLike: () -> Unit,
    onSwitchTab: (FeedTab) -> Unit,
    onCreatorClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(video.backgroundColorHex))
    ) {
        // Top nav: Following / For You tabs
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Following",
                color = if (currentFeedTab == FeedTab.Following) Color.White else Color.White.copy(alpha = 0.6f),
                fontWeight = if (currentFeedTab == FeedTab.Following) FontWeight.Bold else FontWeight.Normal,
                fontSize = 15.sp,
                modifier = Modifier.clickable { onSwitchTab(FeedTab.Following) }
            )
            Text(
                text = "For You",
                color = if (currentFeedTab == FeedTab.ForYou) Color.White else Color.White.copy(alpha = 0.6f),
                fontWeight = if (currentFeedTab == FeedTab.ForYou) FontWeight.Bold else FontWeight.Normal,
                fontSize = 15.sp,
                modifier = Modifier.clickable { onSwitchTab(FeedTab.ForYou) }
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
                    .background(Color.White.copy(alpha = 0.25f))
                    .clickable(onClick = onCreatorClick),
                contentAlignment = Alignment.Center
            ) {
                Text(text = video.author.take(2).uppercase(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

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
                fontSize = 16.sp,
                modifier = Modifier.clickable(onClick = onCreatorClick)
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
private fun ProfileModal(
    userId: String,
    isFollowing: Boolean,
    onClose: () -> Unit,
    onFollow: () -> Unit,
    onUnfollow: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(Color(0xFF1A1A2E), shape = RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = userId.take(2).uppercase(), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = userId,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Researcher",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isFollowing) {
                OutlinedButton(
                    onClick = onUnfollow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Unfollow")
                }
            } else {
                Button(
                    onClick = onFollow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Follow")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Close")
            }
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
                        backgroundColorHex = 0xFF1A1A2E,
                        authorId = "user_1"
                    )
                )
            ),
            onIntent = {}
        )
    }
}
