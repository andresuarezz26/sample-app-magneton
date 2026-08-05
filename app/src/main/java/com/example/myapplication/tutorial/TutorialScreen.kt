package com.example.myapplication.tutorial

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun TutorialScreen(viewModel: TutorialViewModel = viewModel(), onTutorialComplete: () -> Unit = {}) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    TutorialContent(state = state, onIntent = viewModel::onIntent, onComplete = onTutorialComplete)
}

@Composable
private fun TutorialContent(
    state: TutorialUiState,
    onIntent: (TutorialIntent) -> Unit,
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = state.currentPage, pageCount = { state.totalPages })

    LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> WelcomePage()
                1 -> BrowsePage()
                2 -> InteractPage()
                3 -> UploadPage()
            }
        }

        // Skip button (top-right, visible on pages 0-2)
        if (state.currentPage < 3) {
            TextButton(
                onClick = {
                    onIntent(TutorialIntent.SkipTutorial)
                    onComplete()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 8.dp, end = 8.dp)
            ) {
                Text(text = "Skip", fontSize = 14.sp)
            }
        }

        // Page indicator (bottom-center)
        TutorialPageIndicator(
            currentPage = state.currentPage,
            totalPages = state.totalPages,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        )

        // Navigation bar (bottom)
        TutorialNavigationBar(
            currentPage = state.currentPage,
            totalPages = state.totalPages,
            onNext = { onIntent(TutorialIntent.NextPage) },
            onSkip = { onIntent(TutorialIntent.SkipTutorial) },
            onDone = {
                onIntent(TutorialIntent.CompleteTutorial)
                onComplete()
            },
            onIntent = onIntent,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp)
        )
    }
}

@Composable
private fun WelcomePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎉",
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Text(
            text = "Welcome to Discover",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Explore amazing content, interact with creators, and share what you love.",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BrowsePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF16213E))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📹",
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Text(
            text = "Browse Videos",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Swipe up or down to discover new content. Each video has details about the creator and music.",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun InteractPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F3460))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "💬",
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Text(
            text = "Interact & Engage",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Like, comment, and share videos. ❤️ Tap to like. 💬 Comment and ➡️ share with friends.",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun UploadPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF533483))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📤",
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Text(
            text = "Share Your Photos",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Upload multiple photos at once. Select from your gallery and share them with the community.",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TutorialPageIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalPages) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == currentPage) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPage) Color.White
                        else Color.White.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

@Composable
private fun TutorialNavigationBar(
    currentPage: Int,
    totalPages: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onDone: () -> Unit,
    onIntent: (TutorialIntent) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left spacer or back button
        if (currentPage > 0) {
            Text(
                text = "←",
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onIntent(TutorialIntent.PreviousPage)
                    }
            )
        } else {
            Box(modifier = Modifier.size(24.dp))
        }

        // Center action text
        Text(
            text = if (currentPage < totalPages - 1) "${currentPage + 1}/$totalPages" else "Complete",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.6f)
        )

        // Right action button
        Button(
            onClick = {
                if (currentPage < totalPages - 1) {
                    onNext()
                } else {
                    onDone()
                }
            }
        ) {
            Text(
                text = if (currentPage < totalPages - 1) "Next" else "Done",
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun TutorialWelcomePreview() {
    MyApplicationTheme {
        TutorialContent(
            state = TutorialUiState(currentPage = 0),
            onIntent = {},
            onComplete = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun TutorialBrowsePreview() {
    MyApplicationTheme {
        TutorialContent(
            state = TutorialUiState(currentPage = 1),
            onIntent = {},
            onComplete = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun TutorialInteractPreview() {
    MyApplicationTheme {
        TutorialContent(
            state = TutorialUiState(currentPage = 2),
            onIntent = {},
            onComplete = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun TutorialUploadPreview() {
    MyApplicationTheme {
        TutorialContent(
            state = TutorialUiState(currentPage = 3),
            onIntent = {},
            onComplete = {}
        )
    }
}
