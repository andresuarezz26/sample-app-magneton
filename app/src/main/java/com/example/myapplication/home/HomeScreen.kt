package com.example.myapplication.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    val pagerState = rememberPagerState(pageCount = { state.videos.size })

    LaunchedEffect(state.selectedTopic) {
        if (state.videos.isNotEmpty()) {
            pagerState.scrollToPage(0)
        }
    }

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
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                if (page < state.videos.size) {
                    val video = state.videos[page]
                    VideoPage(
                        video = video,
                        onLike = { onIntent(HomeIntent.LikeVideo(video.id)) }
                    )
                }
            }

            TopicChipRow(
                topics = state.topics,
                selectedTopic = state.selectedTopic,
                onSelectTopic = { topic -> onIntent(HomeIntent.SelectTopic(topic)) },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun TopicChipRow(
    topics: List<String>,
    selectedTopic: String?,
    onSelectTopic: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            TopicChip(
                label = "For You",
                selected = selectedTopic == null,
                onClick = { onSelectTopic(null) }
            )
        }
        items(topics) { topic ->
            TopicChip(
                label = topic,
                selected = selectedTopic == topic,
                onClick = { onSelectTopic(topic) }
            )
        }
    }
}

@Composable
private fun TopicChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Color.White else Color.White.copy(alpha = 0.2f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (selected) Color.Black else Color.White,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun VideoPage(video: VideoItem, onLike: () -> Unit) {
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
                        topic = "Physics"
                    )
                )
            ),
            onIntent = {}
        )
    }
}
