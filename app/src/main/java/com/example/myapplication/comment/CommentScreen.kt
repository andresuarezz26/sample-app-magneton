package com.example.myapplication.comment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.Comment
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun CommentScreen(viewModel: CommentViewModel = viewModel(), onBack: () -> Unit = {}) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CommentContent(state = state, onBack = onBack)
}

@Composable
private fun CommentContent(state: CommentUiState, onBack: () -> Unit) {
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
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.comments) { comment ->
                        CommentRow(comment = comment)
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentRow(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.username.take(1).uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = comment.username, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = comment.text, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "${comment.timestampLabel} · ${comment.likes} likes",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommentScreenPreview() {
    MyApplicationTheme {
        CommentContent(
            state = CommentUiState(
                isLoading = false,
                comments = listOf(
                    Comment(
                        id = "1",
                        username = "ana.codes",
                        text = "This is amazing!",
                        timestampLabel = "2h",
                        likes = 14
                    ),
                    Comment(
                        id = "2",
                        username = "maria_dev",
                        text = "Totally agree with this take",
                        timestampLabel = "5h",
                        likes = 3
                    )
                )
            ),
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CommentScreenLoadingPreview() {
    MyApplicationTheme {
        CommentContent(state = CommentUiState(isLoading = true), onBack = {})
    }
}
