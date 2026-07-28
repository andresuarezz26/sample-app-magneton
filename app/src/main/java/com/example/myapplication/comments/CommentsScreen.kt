package com.example.myapplication.comments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.Comment
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun CommentsScreen(
    videoId: String,
    viewModel: CommentsViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(videoId) {
        viewModel.onIntent(CommentsIntent.LoadComments(videoId))
    }

    CommentsContent(state = state, onIntent = viewModel::onIntent, onBack = onBack)
}

@Composable
private fun CommentsContent(
    state: CommentsUiState,
    onIntent: (CommentsIntent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    fontSize = 22.sp,
                    modifier = Modifier.clickable { onBack() }
                )
                Text(
                    text = "Comments",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                state.comments.isEmpty() -> Text(
                    text = "No comments yet",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(state.comments) { comment ->
                        CommentItem(
                            comment = comment,
                            onLike = { onIntent(CommentsIntent.LikeComment(comment.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentItem(comment: Comment, onLike: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = comment.author, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "  ${comment.timestampLabel}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = comment.text,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
        Row(
            modifier = Modifier
                .padding(top = 6.dp)
                .clickable(onClick = onLike),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "❤️", fontSize = 12.sp)
            Text(
                text = "${comment.likes}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommentsScreenLoadingPreview() {
    MyApplicationTheme {
        CommentsContent(state = CommentsUiState(isLoading = true), onIntent = {}, onBack = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun CommentsScreenPopulatedPreview() {
    MyApplicationTheme {
        CommentsContent(
            state = CommentsUiState(
                comments = listOf(
                    Comment(
                        id = "1",
                        videoId = "1",
                        author = "@spacefan22",
                        text = "This blew my mind, had no idea black holes could do that!",
                        likes = 342,
                        timestampLabel = "2h ago"
                    ),
                    Comment(
                        id = "2",
                        videoId = "1",
                        author = "@curious_cat",
                        text = "Wait so does that mean black holes eventually disappear?",
                        likes = 128,
                        timestampLabel = "3h ago"
                    )
                )
            ),
            onIntent = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CommentsScreenEmptyPreview() {
    MyApplicationTheme {
        CommentsContent(state = CommentsUiState(), onIntent = {}, onBack = {})
    }
}
