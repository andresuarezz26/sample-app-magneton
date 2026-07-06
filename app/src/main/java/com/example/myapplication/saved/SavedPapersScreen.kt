package com.example.myapplication.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.VideoItem
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun SavedPapersScreen(viewModel: SavedPapersViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SavedPapersContent(state = state)
}

@Composable
private fun SavedPapersContent(state: SavedPapersUiState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (state.papers.isEmpty()) {
            EmptyState(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                state.papers.forEach { paper -> SavedPaperRow(paper) }
            }
        }
    }
}

@Composable
private fun SavedPaperRow(paper: VideoItem) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = paper.author, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(text = paper.description, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Color.White.copy(alpha = 0.1f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🔖", fontSize = 32.sp)
        }
        Text(
            text = "No saved papers yet",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Tap the bookmark icon to save papers you like",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SavedPapersScreenEmptyPreview() {
    MyApplicationTheme {
        SavedPapersContent(state = SavedPapersUiState())
    }
}
